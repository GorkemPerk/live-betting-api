package com.gorkem.livebettingapi.infrastructure.scheduler;

import com.gorkem.livebettingapi.application.BulletinService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventBettingOddsScheduler {
    private final BulletinService bulletinService;

    @Scheduled(cron = "#{@appConfig.updateOddsCronExpression}")
    public void statusUpdate() {
        bulletinService.updateEventBettingOdds();
    }
}
