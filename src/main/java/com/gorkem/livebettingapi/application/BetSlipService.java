package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.entity.BetSlip;
import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.exception.BadRequestException;
import com.gorkem.livebettingapi.domain.exception.BusinessException;
import com.gorkem.livebettingapi.domain.exception.NotFoundException;
import com.gorkem.livebettingapi.domain.model.command.BetSlipCommand;
import com.gorkem.livebettingapi.domain.model.dto.BetSlipDTO;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.dto.PageableEventDTO;
import com.gorkem.livebettingapi.domain.model.enums.BetType;
import com.gorkem.livebettingapi.domain.model.mapper.ModelMapper;
import com.gorkem.livebettingapi.domain.model.query.PageableQuery;
import com.gorkem.livebettingapi.infrastructure.config.AppConfig;
import com.gorkem.livebettingapi.infrastructure.persistence.BetSlipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.gorkem.livebettingapi.domain.constant.ErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetSlipService {
    private final BetSlipRepository betSlipRepository;
    private final BulletinReadService bulletinReadService;
    private final OddsSnapshotService oddsSnapshotService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;
    private final AppConfig appConfig;

    @Transactional(timeout = 2)
    public BetSlipDTO createSlip(BetSlipCommand command, String customerId) {
        long start = System.currentTimeMillis();
        customerService.getCustomerById(customerId);

        EventDTO eventDTO = getEvents().stream().filter(x -> x.getId().equals(command.getEventId())).findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND, "event not found"));

        Long eventId = eventDTO.getId();

        Double currentOdds = getSelectedOdds(eventDTO, command.getBetType());
        log.info("Current odds is {}", currentOdds);

        oddsSnapshotService.lockOdds(eventId, currentOdds);

        Double lockedOdds = oddsSnapshotService.getLockedOdds(eventId);
        log.info("Locked odds is {}", lockedOdds);

        if (!lockedOdds.equals(command.getSelectedOdds())) {
            throw new BadRequestException(BET_ODDS_CHANGED, String.format("The betting odds have changed. Updated odds: %f", lockedOdds));
        }

        validateMultipleCouponLimit(eventDTO.getId(), command.getCouponCount());
        validateTotalInvestment(command.getAmount(), command.getCouponCount());


        if (System.currentTimeMillis() - start > appConfig.getTimeoutMillisecond()) {
            throw new BadRequestException(BET_SLIP_TIMEOUT, "The operation has timed out.");
        }

        Event event = modelMapper.eventDTOToEvent(eventDTO);

        BetSlip betSlip = betSlip(command, customerId, event, lockedOdds);

        betSlipRepository.save(betSlip);

        oddsSnapshotService.releaseOdds(event.getId());

        return modelMapper.betSlipToBetSlipDTO(betSlip, eventDTO);
    }

    private void validateMultipleCouponLimit(Long eventId, int requestedMultiple) {
        int existing = betSlipRepository.countByEventId(eventId);
        if (existing + requestedMultiple > appConfig.getMaxCouponCount()) {
            throw new BusinessException(MAX_COUPON_LIMIT, String.format("A maximum of %d multiple coupons can be placed for this event.", appConfig.getMaxCouponCount()));
        }
    }

    private void validateTotalInvestment(Double amount, int multiple) {
        Double total = amount * multiple;
        if (total.compareTo(appConfig.getMaxInvestmentLimit()) > 0) {
            throw new BusinessException(MAX_INVESTMENT_LIMIT, String.format("The total investment amount cannot exceed %f TL.", appConfig.getMaxInvestmentLimit()));
        }
    }

    private Double getSelectedOdds(EventDTO eventDTO, BetType type) {
        return switch (type) {
            case HOME_WIN -> eventDTO.getHomeBettingOdds();
            case DRAW -> eventDTO.getDrawBettingOdds();
            case AWAY_WIN -> eventDTO.getAwayBettingOdds();
        };
    }

    private BetSlip betSlip(BetSlipCommand command, String customerId, Event event, Double lockedOdds) {
        return betSlipRepository.findByCustomerIdAndEventId(customerId, event.getId())
                .map(existing -> {
                    existing.setBetType(command.getBetType());
                    existing.setLockedOdds(lockedOdds);
                    existing.setCouponCount(command.getCouponCount());
                    existing.setAmount(command.getAmount());
                    existing.setEvent(event);
                    return existing;
                })
                .orElseGet(() -> BetSlip.builder()
                        .customerId(customerId)
                        .betType(command.getBetType())
                        .event(event)
                        .lockedOdds(lockedOdds)
                        .amount(command.getAmount())
                        .couponCount(command.getCouponCount())
                        .build());
    }

    private List<EventDTO> getEvents() {
        List<EventDTO> eventList = new ArrayList<>();
        int totalPages;
        int page = 1;
        do {
            PageableEventDTO eventPage = bulletinReadService.getEvents(PageableQuery.builder().page(page).build());
            eventList.addAll(eventPage.getContent());

            totalPages = eventPage.getTotalPage();
            page++;
        } while (page < totalPages);
        return eventList;
    }
}
