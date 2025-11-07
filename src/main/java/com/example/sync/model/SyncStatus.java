package com.example.sync.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record SyncStatus(Instant lastSuccessfulSync, int pendingRecords, List<SyncEvent> recentEvents) {

    public SyncStatus {
        Objects.requireNonNull(recentEvents, "recentEvents must not be null");
    }
}
