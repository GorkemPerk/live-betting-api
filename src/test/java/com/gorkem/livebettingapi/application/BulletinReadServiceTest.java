package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.exception.NotFoundException;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.dto.PageableEventDTO;
import com.gorkem.livebettingapi.domain.model.mapper.ModelMapper;
import com.gorkem.livebettingapi.domain.model.query.PageableQuery;
import com.gorkem.livebettingapi.infrastructure.persistence.BulletinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulletinReadServiceTest {
    @InjectMocks
    private BulletinReadService sut;
    @Mock
    private BulletinRepository bulletinRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testGetEvent_whenEventExists_returnsEventDTO() {
        Long eventId = 1L;
        Event event = Event.builder().id(eventId).build();
        EventDTO eventDTO = EventDTO.builder().id(eventId).build();

        when(bulletinRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.eventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = sut.getEvent(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
        verify(bulletinRepository).findById(eventId);
        verify(modelMapper).eventToEventDTO(event);
    }

    @Test
    void testGetEvent_whenEventNotFound_throwsNotFoundException() {
        Long eventId = 1L;

        when(bulletinRepository.findById(eventId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            sut.getEvent(eventId);
        });

        assertEquals("Event not found", ex.getMessage());
        verify(bulletinRepository).findById(eventId);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    void testGetEvents_whenContentExists_returnsPageableEventDTO() {
        PageableQuery query = PageableQuery.builder().page(1).size(2).build();

        Event event1 = Event.builder().id(1L).build();
        Event event2 = Event.builder().id(2L).build();
        List<Event> events = List.of(event1, event2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Event> page = new PageImpl<>(events, pageable, 5);

        List<EventDTO> eventDTOs = List.of(
                EventDTO.builder().id(1L).build(),
                EventDTO.builder().id(2L).build()
        );

        when(bulletinRepository.findByStatusIn(anyList(), eq(pageable))).thenReturn(page);
        when(modelMapper.eventsToEventDTO(events)).thenReturn(eventDTOs);

        PageableEventDTO result = sut.getEvents(query);

        assertNotNull(result);
        assertEquals(eventDTOs, result.getContent());
        verify(bulletinRepository).findByStatusIn(anyList(), eq(pageable));
        verify(modelMapper).eventsToEventDTO(events);
    }

    @Test
    void testGetEvents_whenNoContent_returnsEmptyPageableEventDTO() {
        PageableQuery query = PageableQuery.builder().page(1).size(2).build();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Event> emptyPage = Page.empty(pageable);

        when(bulletinRepository.findByStatusIn(anyList(), eq(pageable))).thenReturn(emptyPage);

        PageableEventDTO result = sut.getEvents(query);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(bulletinRepository).findByStatusIn(anyList(), eq(pageable));
        verifyNoMoreInteractions(modelMapper);
    }


}