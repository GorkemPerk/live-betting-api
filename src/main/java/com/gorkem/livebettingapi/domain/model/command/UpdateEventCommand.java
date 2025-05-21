package com.gorkem.livebettingapi.domain.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventCommand {
    private BettingOddsCommand bettingOdds;
}
