package com.gorkem.livebettingapi.domain.model.dto;


import com.gorkem.livebettingapi.domain.model.enums.EventType;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String league;
    private String homeTeamName;
    private String awayTeamName;
    private EventType eventType;
    private Double homeBettingOdds;
    private Double drawBettingOdds;
    private Double awayBettingOdds;
    private LocalDateTime startDate;
    private Status status;
}
