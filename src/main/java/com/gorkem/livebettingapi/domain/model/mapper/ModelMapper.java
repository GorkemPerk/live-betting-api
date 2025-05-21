package com.gorkem.livebettingapi.domain.model.mapper;

import com.gorkem.livebettingapi.domain.entity.BetSlip;
import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.model.command.EventCommand;
import com.gorkem.livebettingapi.domain.model.command.UpdateEventCommand;
import com.gorkem.livebettingapi.domain.model.dto.BetSlipDTO;
import com.gorkem.livebettingapi.domain.model.dto.EventDTO;
import com.gorkem.livebettingapi.domain.model.enums.EventType;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ModelMapper {
    @Mappings({@Mapping(source = "bettingOdds.home", target = "homeBettingOdds"),
            @Mapping(source = "bettingOdds.draw", target = "drawBettingOdds"),
            @Mapping(source = "bettingOdds.away", target = "awayBettingOdds"),
            @Mapping(target = "endDate", expression = "java(mapEndDate(command.getEventType(), command.getStartDate()))"),
            @Mapping(target = "status", expression = "java(mapStatus())")
    })
    Event eventCommandToEvent(EventCommand command);

    @Mappings({@Mapping(source = "bettingOdds.home", target = "homeBettingOdds"),
            @Mapping(source = "bettingOdds.draw", target = "drawBettingOdds"),
            @Mapping(source = "bettingOdds.away", target = "awayBettingOdds")})
    void updateEventFromUpdateEventCommand(UpdateEventCommand updateEventCommand, @MappingTarget Event event);

    EventDTO eventToEventDTO(Event event);

    default LocalDateTime mapEndDate(EventType eventType, LocalDateTime startDate) {
        return startDate.plusMinutes(eventType.getDurationMinutes());
    }

    default Status mapStatus() {
        return Status.PENDING;
    }

    List<EventDTO> eventsToEventDTO(List<Event> event);

    BetSlipDTO betSlipToBetSlipDTO(BetSlip betSlip, EventDTO eventDTO);

    Event eventDTOToEvent(EventDTO eventDTO);
}
