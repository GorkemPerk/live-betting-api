package com.gorkem.livebettingapi.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gorkem.livebettingapi.application.BulletinService;
import com.gorkem.livebettingapi.domain.model.command.BettingOddsCommand;
import com.gorkem.livebettingapi.domain.model.command.EventCommand;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BulletinControllerTest {
    @InjectMocks
    private BulletinController sut;

    private MockMvc mockMvc;
    @Mock
    private BulletinService bulletinService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private EventCommand command;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();

        command = EventCommand.builder()
                .league("League")
                .eventType(EventType.FOOTBALL)
                .homeTeamName("besiktas")
                .awayTeamName("galatasaray")
                .bettingOdds(BettingOddsCommand.builder()
                        .away(3.0)
                        .home(2.0)
                        .draw(3.2)
                        .build())
                .startDate(LocalDateTime.now().plusDays(1))
                .build();

        eventDTO = EventDTO.builder()
                .id(1L)
                .league("League")
                .homeTeamName("besiktas")
                .awayTeamName("galatasaray")
                .homeBettingOdds(2.0)
                .drawBettingOdds(3.2)
                .awayBettingOdds(3.0)
                .build();
    }

    @Test
    void createEvent_shouldReturnCreatedAndEventDTO() throws Exception {
        when(bulletinService.createEvent(command)).thenReturn(eventDTO);

        mockMvc.perform(post("/api/v1/bulletin/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.homeTeamName").value("besiktas"))
                .andExpect(jsonPath("$.data.awayTeamName").value("galatasaray"));

        verify(bulletinService).createEvent(command);
    }

    @Test
    void updateEventBettingOdds_shouldReturnCreated() throws Exception {
        doNothing().when(bulletinService).updateEventBettingOdds();

        mockMvc.perform(put("/api/v1/bulletin/event/betting-odds"))
                .andExpect(status().isCreated());

        verify(bulletinService).updateEventBettingOdds();
    }

    @Test
    void updateEventStatus_shouldReturnCreated() throws Exception {
        doNothing().when(bulletinService).updateStatuses();

        mockMvc.perform(patch("/api/v1/bulletin/event/status"))
                .andExpect(status().isCreated());

        verify(bulletinService).updateStatuses();
    }
}