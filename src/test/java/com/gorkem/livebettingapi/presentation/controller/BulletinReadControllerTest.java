package com.gorkem.livebettingapi.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkem.livebettingapi.application.BulletinReadService;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.dto.PageableEventDTO;
import com.gorkem.livebettingapi.domain.model.query.PageableQuery;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BulletinReadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BulletinReadService bulletinReadService;

    @InjectMocks
    private BulletinReadController bulletinReadController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EventDTO eventDTO;
    private PageableEventDTO pageableEventDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bulletinReadController).build();

        eventDTO = EventDTO.builder()
                .id(1L)
                .league("turkey")
                .homeTeamName("besiktas")
                .awayTeamName("galatasaray")
                .homeBettingOdds(2.1)
                .drawBettingOdds(3.4)
                .awayBettingOdds(2.9)
                .build();

        pageableEventDTO = PageableEventDTO.builder()
                .content(List.of(eventDTO))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPage(1)
                .build();
    }

    @Test
    void getEvent_shouldReturnEventDTO() throws Exception {
        Long eventId = 1L;
        when(bulletinReadService.getEvent(eventId)).thenReturn(eventDTO);

        mockMvc.perform(get("/api/v1/bulletin-read/event/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.league").value("turkey"))
                .andExpect(jsonPath("$.data.homeTeamName").value("besiktas"))
                .andExpect(jsonPath("$.data.awayTeamName").value("galatasaray"));
    }

    @Test
    void getEvents_shouldReturnPageableEventDTO() throws Exception {
        when(bulletinReadService.getEvents(any(PageableQuery.class))).thenReturn(pageableEventDTO);

        mockMvc.perform(get("/api/v1/bulletin-read/events")
                        .param("page", "1")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}