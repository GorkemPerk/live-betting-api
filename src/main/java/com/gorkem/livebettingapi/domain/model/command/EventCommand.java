package com.gorkem.livebettingapi.domain.model.command;

import com.gorkem.livebettingapi.domain.model.enums.EventType;
import com.gorkem.livebettingapi.presentation.validator.ValidStartDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCommand {
    @NotBlank(message = "League cannot be blank")
    private String league;
    @NotBlank(message = "Home Team Name cannot be blank")
    private String homeTeamName;
    @NotBlank(message = "Away Team Name cannot be blank")
    private String awayTeamName;
    @NotNull(message = "Event type cannot be null")
    private EventType eventType;
    @NotNull(message = "Odds cannot be null")
    private BettingOddsCommand bettingOdds;
    @NotNull(message = "Start date cannot be null")
    @ValidStartDate(message = "Start date must be greater than time 30 minutes ahead")
    private LocalDateTime startDate;
}
