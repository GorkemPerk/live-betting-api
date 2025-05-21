package com.gorkem.livebettingapi.domain.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BettingOddsCommand {
    private Double home;
    private Double draw;
    private Double away;
}
