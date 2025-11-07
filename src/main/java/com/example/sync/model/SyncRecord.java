package com.example.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.Objects;

public record SyncRecord(String key, String value, Instant lastModified) {

    public SyncRecord {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(lastModified, "lastModified must not be null");
    }

    public SyncRecord withValue(String newValue, Instant updatedAt) {
        return new SyncRecord(key, newValue, updatedAt);
    }

    @JsonIgnore
    public String toDisplayString() {
        return key + " => " + value;
    }
}
