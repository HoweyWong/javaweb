package com.example.sync.model;

import java.time.Instant;
import java.util.Objects;

public record SyncEvent(Instant timestamp, String message) {

    public SyncEvent {
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }
}
