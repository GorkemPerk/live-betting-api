package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.entity.BetSlip;
import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.exception.BadRequestException;
import com.gorkem.livebettingapi.domain.exception.BusinessException;
import com.gorkem.livebettingapi.domain.model.command.BetSlipCommand;
import com.gorkem.livebettingapi.domain.model.dto.BetSlipDTO;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.dto.PageableEventDTO;
import com.gorkem.livebettingapi.domain.model.enums.BetType;
import com.gorkem.livebettingapi.domain.model.mapper.ModelMapper;
import com.gorkem.livebettingapi.infrastructure.config.AppConfig;
import com.gorkem.livebettingapi.infrastructure.persistence.BetSlipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetSlipServiceTest {
    @InjectMocks
    private BetSlipService sut;
    @Mock
    private BetSlipRepository betSlipRepository;
    @Mock
    private BulletinReadService bulletinReadService;
    @Mock
    private OddsSnapshotService oddsSnapshotService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AppConfig appConfig;

    private BetSlipCommand command;
    private EventDTO eventDTO;
    private Event event;
    private BetSlip betSlip;
    private String customerId;

    @BeforeEach
    void setUp() {
        customerId = "1";

        command = BetSlipCommand.builder()
                .eventId(1L)
                .couponCount(1)
                .amount(100.0)
                .betType(BetType.HOME_WIN)
                .selectedOdds(2.5)
                .build();

        eventDTO = EventDTO.builder()
                .id(1L)
                .homeBettingOdds(2.5)
                .drawBettingOdds(3.0)
                .awayBettingOdds(4.0)
                .build();

        event = Event.builder()
                .id(1L)
                .build();
    }

    @Test
    void createSlip_ShouldReturnBetSlipDTO_WhenDataIsValid() {
        when(appConfig.getTimeoutMillisecond()).thenReturn(2000);
        when(appConfig.getMaxCouponCount()).thenReturn(10);
        when(appConfig.getMaxInvestmentLimit()).thenReturn(10000.0);

        when(bulletinReadService.getEvents(any())).thenReturn(PageableEventDTO.builder()
                .content(List.of(eventDTO))
                .page(1)
                .totalPage(2)
                .build());

        when(oddsSnapshotService.getLockedOdds(1L)).thenReturn(2.5);
        when(modelMapper.eventDTOToEvent(eventDTO)).thenReturn(event);
        when(betSlipRepository.findByCustomerIdAndEventId(anyString(), eq(1L))).thenReturn(Optional.empty());
        when(betSlipRepository.countByEventId(1L)).thenReturn(0);
        when(betSlipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(modelMapper.betSlipToBetSlipDTO(any(), any())).thenReturn(new BetSlipDTO());

        BetSlipDTO result = sut.createSlip(command, customerId);

        assertNotNull(result);
        verify(oddsSnapshotService).lockOdds(1L, 2.5);
        verify(oddsSnapshotService).releaseOdds(1L);
        verify(customerService).getCustomerById(customerId);
    }

    @Test
    void createSlip_ShouldThrowBadRequestException_WhenOddsChanged() {
        command.setSelectedOdds(3.0);

        when(bulletinReadService.getEvents(any())).thenReturn(PageableEventDTO.builder()
                .content(List.of(eventDTO))
                .page(1)
                .totalPage(2)
                .build());

        when(oddsSnapshotService.getLockedOdds(1L)).thenReturn(2.5);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> sut.createSlip(command, customerId));

        assertTrue(exception.getMessage().contains("betting odds have changed"));
    }

    @Test
    void createSlip_ShouldThrowBusinessException_WhenCouponLimitExceeded() {
        when(appConfig.getMaxCouponCount()).thenReturn(10);
        when(bulletinReadService.getEvents(any())).thenReturn(PageableEventDTO.builder()
                .content(List.of(eventDTO))
                .page(1)
                .totalPage(2)
                .build());

        when(oddsSnapshotService.getLockedOdds(1L)).thenReturn(2.5);
        when(betSlipRepository.countByEventId(1L)).thenReturn(10);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sut.createSlip(command, customerId));

        assertTrue(exception.getMessage().contains("multiple coupons"));
    }

    @Test
    void createSlip_ShouldThrowBusinessException_WhenInvestmentLimitExceeded() {
        when(appConfig.getMaxCouponCount()).thenReturn(1000);
        when(appConfig.getMaxInvestmentLimit()).thenReturn(10000.0);
        command.setAmount(6000.0);
        command.setCouponCount(2);

        when(bulletinReadService.getEvents(any())).thenReturn(PageableEventDTO.builder()
                .content(List.of(eventDTO))
                .page(1)
                .totalPage(2)
                .build());

        when(oddsSnapshotService.getLockedOdds(1L)).thenReturn(2.5);
        when(betSlipRepository.countByEventId(1L)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sut.createSlip(command, customerId));

        assertTrue(exception.getMessage().contains("total investment amount"));
    }


}