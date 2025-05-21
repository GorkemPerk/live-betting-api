package com.gorkem.livebettingapi.domain.entity;

import com.gorkem.livebettingapi.domain.model.enums.EventType;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event extends AuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @Column(name = "league", nullable = false)
    private String league;
    @Column(name = "home_team_name", nullable = false)
    private String homeTeamName;
    @Column(name = "away_team_name", nullable = false)
    private String awayTeamName;
    @Column(name = "status", nullable = false)
    private Status status;
    @Column(name = "event_type", nullable = false)
    private EventType eventType;
    @Column(name = "home_betting_odds", nullable = false)
    private Double homeBettingOdds;
    @Column(name = "draw_betting_odds", nullable = false)
    private Double drawBettingOdds;
    @Column(name = "away_betting_odds", nullable = false)
    private Double awayBettingOdds;
    @Column(name = "start_Date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_Date", nullable = false)
    private LocalDateTime endDate;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;
}
