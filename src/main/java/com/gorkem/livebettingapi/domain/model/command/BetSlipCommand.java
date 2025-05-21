package com.gorkem.livebettingapi.domain.model.command;

import com.gorkem.livebettingapi.domain.model.enums.BetType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetSlipCommand {
    @NotNull(message = "Event id cannot be null")
    private Long eventId;
    @NotNull(message = "Bet type cannot be null")
    private BetType betType;
    @NotNull(message = "Coupon count cannot be null")
    private Integer couponCount;
    @NotNull(message = "Amount cannot be null")
    private Double amount;
    @NotNull(message = "Selected odds cannot be null")
    private Double selectedOdds;
}
