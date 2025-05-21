package com.gorkem.livebettingapi.application;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OddsSnapshotService {
    private final Map<Long, Double> lockedOddsMap = new ConcurrentHashMap<>();

    public void lockOdds(Long eventId, Double odds) {
        lockedOddsMap.put(eventId, odds);
    }

    public Double getLockedOdds(Long eventId) {
        return lockedOddsMap.get(eventId);
    }

    public boolean isOddsLocked(Long eventId) {
        return lockedOddsMap.containsKey(eventId);
    }
    public void releaseOdds(Long eventId) {
        lockedOddsMap.remove(eventId);
    }
}
