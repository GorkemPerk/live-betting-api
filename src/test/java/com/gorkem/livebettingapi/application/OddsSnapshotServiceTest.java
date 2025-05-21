package com.gorkem.livebettingapi.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OddsSnapshotServiceTest {
    @InjectMocks
    private OddsSnapshotService sut;

    @Test
    void testLockAndGetLockedOdds() {
        Long eventId = 1L;
        Double odds = 1.0;

        sut.lockOdds(eventId, odds);
        assertTrue(sut.isOddsLocked(eventId), "Odds should be locked");
        assertEquals(odds, sut.getLockedOdds(eventId), "Locked odds should match");
    }

    @Test
    void testIsOddsLocked_whenNotLocked() {
        Long eventId = 1L;
        assertFalse(sut.isOddsLocked(eventId), "Odds should not be locked");
    }

    @Test
    void testReleaseOdds() {
        Long eventId = 1L;
        Double odds = 1.10;
        sut.lockOdds(eventId, odds);
        assertTrue(sut.isOddsLocked(eventId));
        sut.releaseOdds(eventId);
        assertFalse(sut.isOddsLocked(eventId), "Odds should be released");
        assertNull(sut.getLockedOdds(eventId), "No odds should be present after release");
    }
}