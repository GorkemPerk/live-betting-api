package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.exception.BadRequestException;
import com.gorkem.livebettingapi.domain.model.command.EventCommand;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import com.gorkem.livebettingapi.domain.model.mapper.ModelMapper;
import com.gorkem.livebettingapi.infrastructure.config.AppConfig;
import com.gorkem.livebettingapi.infrastructure.persistence.BulletinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulletinServiceTest {
    @InjectMocks
    private BulletinService sut;
    @Mock
    private BulletinRepository bulletinRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AppConfig appConfig;

    @Mock
    private OddsSnapshotService oddsSnapshotService;

    private EventCommand eventCommand;
    private Event event;
    private EventDTO eventDTO;

    @BeforeEach
    void setup() {
        eventCommand = EventCommand.builder()
                .homeTeamName("Team A")
                .awayTeamName("Team B")
                .build();

        event = Event.builder()
                .id(1L)
                .homeTeamName("besiktas")
                .awayTeamName("galatasaray")
                .status(Status.PENDING)
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(3))
                .homeBettingOdds(1.5)
                .drawBettingOdds(2.5)
                .awayBettingOdds(3.5)
                .build();

        eventDTO = EventDTO.builder()
                .id(1L)
                .build();
    }

    @Test
    void createEvent_WhenEventExists_ShouldThrowException() {
        when(bulletinRepository.findByHomeTeamNameAndAwayTeamName(anyString(), anyString()))
                .thenReturn(Optional.of(event));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            sut.createEvent(eventCommand);
        });
        assertEquals("Event already exists", ex.getMessage());
    }

    @Test
    void createEvent_WhenEventNotExists_ShouldSaveAndReturnDTO() {
        when(bulletinRepository.findByHomeTeamNameAndAwayTeamName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        when(modelMapper.eventCommandToEvent(eventCommand)).thenReturn(event);
        when(modelMapper.eventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = sut.createEvent(eventCommand);

        verify(bulletinRepository).save(event);
        assertEquals(eventDTO, result);
    }

    @Test
    void updateEventBettingOdds_ShouldSkipLockedEventsAndUpdateOthers() {
        Event eventMock = Event.builder()
                .id(1L)
                .status(Status.ACTIVE)
                .homeBettingOdds(1.5)
                .drawBettingOdds(2.5)
                .awayBettingOdds(3.5)
                .build();

        Event event = Event.builder()
                .id(2L)
                .status(Status.PENDING)
                .homeBettingOdds(1.7)
                .drawBettingOdds(2.7)
                .awayBettingOdds(3.7)
                .build();

        when(bulletinRepository.findByStatusIn(anyList()))
                .thenReturn(List.of(eventMock, event));

        when(oddsSnapshotService.isOddsLocked(1L)).thenReturn(true);
        when(oddsSnapshotService.isOddsLocked(2L)).thenReturn(false);

        when(appConfig.getMinOdds()).thenReturn(1.01);
        when(appConfig.getMaxOdds()).thenReturn(10.0);

        sut.updateEventBettingOdds();

        verify(bulletinRepository).saveAll(argThat((List<Event> events) ->
                events.size() == 1 && events.getFirst().getId() == 2L));
    }

    @Test
    void updateStatuses_ShouldUpdateEventStatuses() {
        LocalDateTime now = LocalDateTime.now();
        Event eventMock = Event.builder()
                .id(1L)
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .status(Status.ACTIVE)
                .build();

        Event event = Event.builder()
                .id(2L)
                .startDate(now.minusHours(1))
                .endDate(now.plusHours(1))
                .status(Status.PENDING)
                .build();

        when(bulletinRepository.findAll()).thenReturn(List.of(eventMock, event));

        sut.updateStatuses();

        verify(bulletinRepository).saveAll(argThat((List<Event> list) -> !list.isEmpty()));
    }
}