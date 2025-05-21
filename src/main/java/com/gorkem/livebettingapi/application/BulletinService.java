package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.exception.BadRequestException;
import com.gorkem.livebettingapi.domain.model.command.EventCommand;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import com.gorkem.livebettingapi.domain.model.mapper.ModelMapper;
import com.gorkem.livebettingapi.infrastructure.config.AppConfig;
import com.gorkem.livebettingapi.infrastructure.persistence.BulletinRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulletinService {
    private final BulletinRepository bulletinRepository;
    private final ModelMapper modelMapper;
    private final AppConfig appConfig;
    private final OddsSnapshotService oddsSnapshotService;

    public EventDTO createEvent(EventCommand command) {
        bulletinRepository.findByHomeTeamNameAndAwayTeamName(command.getHomeTeamName(), command.getAwayTeamName())
                .ifPresent(event -> {
                    throw new BadRequestException("Event already exists");
                });
        Event event = modelMapper.eventCommandToEvent(command);
        bulletinRepository.save(event);
        return modelMapper.eventToEventDTO(event);
    }

    @Transactional
    public void updateEventBettingOdds() {
        List<Event> events = bulletinRepository.findByStatusIn(List.of(Status.PENDING, Status.ACTIVE));
        List<Event> eventsToUpdate = new ArrayList<>();

        for (Event event : events) {
            if (oddsSnapshotService.isOddsLocked(event.getId())) {
                continue;
            }
            event.setHomeBettingOdds(updateRandomBet(event.getHomeBettingOdds()));
            event.setDrawBettingOdds(updateRandomBet(event.getDrawBettingOdds()));
            event.setAwayBettingOdds(updateRandomBet(event.getAwayBettingOdds()));
            eventsToUpdate.add(event);
        }

        if (!eventsToUpdate.isEmpty()) {
            bulletinRepository.saveAll(eventsToUpdate);
            System.out.println("Event betting odds updated for " + eventsToUpdate.size() + " events");
        } else {
            System.out.println("No events updated because all are locked");
        }
    }

    private Double updateRandomBet(Double originalOdds) {
        Random random = new Random();
        boolean shouldUpdate = random.nextBoolean();
        if (shouldUpdate) {
            double changePercent = (random.nextDouble() * 0.2) - 0.1;
            double newOdds = originalOdds + (originalOdds * changePercent);

            newOdds = Math.max(appConfig.getMinOdds(), Math.min(newOdds, appConfig.getMaxOdds()));
            return Math.round(newOdds * 100.0) / 100.0;
        }
        return originalOdds;
    }

    @Transactional
    public void updateStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = bulletinRepository.findAll();
        List<Event> toUpdate = new ArrayList<>();

        for (Event event : events) {
            Status newStatus = determineStatus(event, now);
            if (event.getStatus() != newStatus) {
                event.setStatus(newStatus);
                toUpdate.add(event);
            }
        }

        if (!toUpdate.isEmpty()) {
            log.info("Event statuses updated");
            bulletinRepository.saveAll(toUpdate);
        }
    }

    private Status determineStatus(Event event, LocalDateTime now) {
        if (now.isBefore(event.getStartDate())) {
            return Status.PENDING;
        } else if (!now.isAfter(event.getEndDate())) {
            return Status.ACTIVE;
        } else {
            return Status.PASSIVE;
        }
    }
}
