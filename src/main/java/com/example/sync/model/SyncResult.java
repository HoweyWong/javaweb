package com.example.sync.model;

import java.util.List;
import java.util.Objects;

public record SyncResult(int synchronizedCount, List<String> synchronizedKeys) {

    public SyncResult {
        Objects.requireNonNull(synchronizedKeys, "synchronizedKeys must not be null");
    }
}
