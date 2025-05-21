package com.gorkem.livebettingapi.infrastructure.scheduler;

import com.gorkem.livebettingapi.application.BulletinService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final BulletinService bulletinService;

    @Scheduled(cron = "0 */1 * * * *")
    public void runStatusUpdate() {
        bulletinService.updateStatuses();
    }
}
