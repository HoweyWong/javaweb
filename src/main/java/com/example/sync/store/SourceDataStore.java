package com.example.sync.store;

import com.example.sync.model.SyncRecord;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class SourceDataStore {

    private final ConcurrentMap<String, SyncRecord> records = new ConcurrentHashMap<>();
    private final Clock clock;

    public SourceDataStore(Clock clock) {
        this.clock = clock;
    }

    public Collection<SyncRecord> findAll() {
        return records.values();
    }

    public Optional<SyncRecord> findByKey(String key) {
        return Optional.ofNullable(records.get(key));
    }

    public SyncRecord save(String key, String value) {
        SyncRecord record = new SyncRecord(key, value, clock.instant());
        records.put(key, record);
        return record;
    }

    public SyncRecord save(SyncRecord record) {
        records.put(record.key(), record);
        return record;
    }

    public void saveAll(Map<String, String> entries) {
        Instant now = clock.instant();
        entries.forEach((key, value) -> records.put(key, new SyncRecord(key, value, now)));
    }

    public boolean remove(String key) {
        return records.remove(key) != null;
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public void clear() {
        records.clear();
    }
}
