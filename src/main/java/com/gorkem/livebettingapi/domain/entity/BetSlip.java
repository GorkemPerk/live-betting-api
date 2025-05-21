package com.gorkem.livebettingapi.domain.entity;

import com.gorkem.livebettingapi.domain.model.enums.BetType;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bet_slips")
public class BetSlip extends AuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(name = "coupon_count", nullable = false)
    private Integer couponCount;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Column(name = "bet_type", nullable = false)
    private BetType betType;
    @Column(name = "locked_odds", nullable = false)
    private Double lockedOdds;
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;
}
