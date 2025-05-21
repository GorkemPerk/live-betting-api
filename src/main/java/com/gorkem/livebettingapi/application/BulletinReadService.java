package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.exception.NotFoundException;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.dto.PageableEventDTO;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import com.gorkem.livebettingapi.domain.model.mapper.ModelMapper;
import com.gorkem.livebettingapi.domain.model.query.PageableQuery;
import com.gorkem.livebettingapi.infrastructure.persistence.BulletinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulletinReadService {
    private final BulletinRepository bulletinRepository;
    private final ModelMapper modelMapper;


    public EventDTO getEvent(Long id) {
        Event event = bulletinRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        return modelMapper.eventToEventDTO(event);
    }

    public PageableEventDTO getEvents(PageableQuery query) {
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        List<Status> statuses = List.of(Status.PENDING, Status.ACTIVE);
        Page<Event> events = bulletinRepository.findByStatusIn(statuses, pageable);
        if (!events.hasContent()) {
            return PageableEventDTO.builder()
                    .page(query.getPage())
                    .size(query.getSize())
                    .totalElements(events.getTotalElements())
                    .totalPage(events.getTotalPages())
                    .content(Collections.emptyList())
                    .build();
        }
        return PageableEventDTO.builder()
                .page(query.getPage())
                .size(query.getSize())
                .totalElements(events.getTotalElements())
                .totalPage(events.getTotalPages())
                .content(modelMapper.eventsToEventDTO(events.getContent()))
                .build();
    }
}

