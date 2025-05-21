package com.gorkem.livebettingapi.presentation.controller;

import com.gorkem.livebettingapi.application.BulletinService;
import com.gorkem.livebettingapi.domain.model.command.EventCommand;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.presentation.model.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bulletin")
@RequiredArgsConstructor
public class BulletinController {
    private final BulletinService bulletinService;

    @PostMapping("/event")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<EventDTO> createEvent(@Valid @RequestBody EventCommand command) {
        EventDTO eventDTO = bulletinService.createEvent(command);
        return Response.success(eventDTO);
    }

    @PutMapping("/event/betting-odds")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateEventBettingOdds() {
        bulletinService.updateEventBettingOdds();
    }

    @PatchMapping("/event/status")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateEventStatus() {
        bulletinService.updateStatuses();
    }
}
