package com.gorkem.livebettingapi.presentation.controller;

import com.gorkem.livebettingapi.application.BetSlipService;
import com.gorkem.livebettingapi.domain.context.CustomerContext;
import com.gorkem.livebettingapi.domain.model.command.BetSlipCommand;
import com.gorkem.livebettingapi.domain.model.dto.BetSlipDTO;
import com.gorkem.livebettingapi.infrastructure.intercaptor.CustomerInterceptor;
import com.gorkem.livebettingapi.presentation.model.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bet")
@RequiredArgsConstructor
public class BetSlipController {
    private final BetSlipService betSlipService;

    @PostMapping("/slip")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<BetSlipDTO> createSlip(@Valid @RequestBody BetSlipCommand command, HttpServletRequest request) {
        CustomerContext customerContext = (CustomerContext) request.getAttribute(CustomerInterceptor.CUSTOMER_CONTEXT_ATTRIBUTE);
        BetSlipDTO slip = betSlipService.createSlip(command, customerContext.getId());
        return Response.success(slip);
    }
}
