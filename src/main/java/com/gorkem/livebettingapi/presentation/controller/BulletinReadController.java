package com.gorkem.livebettingapi.presentation.controller;

import com.gorkem.livebettingapi.application.BulletinReadService;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.dto.PageableEventDTO;
import com.gorkem.livebettingapi.domain.model.query.PageableQuery;
import com.gorkem.livebettingapi.presentation.model.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bulletin-read")
@RequiredArgsConstructor
public class BulletinReadController {
    private final BulletinReadService bulletinReadService;

    @GetMapping("/event/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<EventDTO> getEvent(@PathVariable Long id) {
        EventDTO event = bulletinReadService.getEvent(id);
        return Response.success(event);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public Response<PageableEventDTO> getEvents(@Valid PageableQuery query) {
        PageableEventDTO events = bulletinReadService.getEvents(query);
        return Response.success(events);
    }
}
