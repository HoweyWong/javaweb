package com.example.sync.store;

import com.example.sync.model.SyncRecord;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class TargetDataStore {

    private final ConcurrentMap<String, SyncRecord> records = new ConcurrentHashMap<>();

    public Collection<SyncRecord> findAll() {
        return records.values();
    }

    public Optional<SyncRecord> findByKey(String key) {
        return Optional.ofNullable(records.get(key));
    }

    public SyncRecord save(SyncRecord record) {
        records.put(record.key(), record);
        return record;
    }

    public boolean remove(String key) {
        return records.remove(key) != null;
    }
}
