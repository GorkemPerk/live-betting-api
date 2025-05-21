package com.gorkem.livebettingapi.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkem.livebettingapi.application.BetSlipService;
import com.gorkem.livebettingapi.domain.context.CustomerContext;
import com.gorkem.livebettingapi.domain.model.command.BetSlipCommand;
import com.gorkem.livebettingapi.domain.model.dto.BetSlipDTO;
import com.gorkem.livebettingapi.domain.model.enums.BetType;
import com.gorkem.livebettingapi.infrastructure.intercaptor.CustomerInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BetSlipControllerTest {
    @InjectMocks
    private BetSlipController sut;

    private MockMvc mockMvc;

    @Mock
    private BetSlipService betSlipService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private BetSlipCommand command;
    private BetSlipDTO slipDTO;

    private static final String BASE_URL = "/api/v1/bet/slip";
    private static final String CUSTOMER_ID = "1";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();

        command = BetSlipCommand.builder()
                .eventId(1L)
                .couponCount(1)
                .amount(100.0)
                .betType(BetType.HOME_WIN)
                .selectedOdds(2.5)
                .build();

        slipDTO = new BetSlipDTO();
    }

    @Test
    void createSlip_shouldReturnCreatedWithSlipDTO() throws Exception {
        when(betSlipService.createSlip(eq(command), eq(CUSTOMER_ID)))
                .thenReturn(slipDTO);

        mockMvc.perform(post(BASE_URL)
                        .requestAttr(CustomerInterceptor.CUSTOMER_CONTEXT_ATTRIBUTE, new CustomerContext(CUSTOMER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
    }
}