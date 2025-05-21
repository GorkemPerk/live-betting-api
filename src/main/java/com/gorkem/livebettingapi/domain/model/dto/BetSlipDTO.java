package com.gorkem.livebettingapi.domain.model.dto;

import com.gorkem.livebettingapi.domain.model.enums.BetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetSlipDTO {
    private String customerId;
    private Integer couponCount;
    private Double amount;
    private EventDTO event;
    private BetType betType;
    private Double lockedOdds;
}
