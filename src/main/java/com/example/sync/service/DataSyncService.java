package com.example.sync.service;

import com.example.sync.model.SyncEvent;
import com.example.sync.model.SyncRecord;
import com.example.sync.model.SyncResult;
import com.example.sync.model.SyncStatus;
import com.example.sync.store.SourceDataStore;
import com.example.sync.store.TargetDataStore;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DataSyncService {

    private static final int MAX_RECENT_EVENTS = 10;

    private final SourceDataStore sourceDataStore;
    private final TargetDataStore targetDataStore;
    private final Clock clock;
    private final Deque<SyncEvent> recentEvents = new ConcurrentLinkedDeque<>();
    private volatile Instant lastSuccessfulSync;

    public DataSyncService(SourceDataStore sourceDataStore, TargetDataStore targetDataStore, Clock clock) {
        this.sourceDataStore = Objects.requireNonNull(sourceDataStore, "sourceDataStore must not be null");
        this.targetDataStore = Objects.requireNonNull(targetDataStore, "targetDataStore must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public List<SyncRecord> previewDifferences() {
        return sourceDataStore.findAll().stream()
            .filter(this::needsSync)
            .sorted(Comparator.comparing(SyncRecord::key))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public SyncResult syncAll() {
        List<SyncRecord> recordsToSync = previewDifferences();
        recordsToSync.forEach(targetDataStore::save);
        if (!recordsToSync.isEmpty()) {
            lastSuccessfulSync = clock.instant();
            recordEvent("同步 " + recordsToSync.size() + " 条记录: "
                + recordsToSync.stream().map(SyncRecord::key).collect(Collectors.joining(", ")));
        } else {
            recordEvent("源和目标已同步，无需操作");
        }
        return new SyncResult(recordsToSync.size(), recordsToSync.stream().map(SyncRecord::key).toList());
    }

    public SyncResult syncRecord(String key) {
        SyncRecord sourceRecord = sourceDataStore.findByKey(key)
            .orElseThrow(() -> new NoSuchElementException("未找到源记录: " + key));
        if (needsSync(sourceRecord)) {
            targetDataStore.save(sourceRecord);
            lastSuccessfulSync = clock.instant();
            recordEvent("同步单条记录: " + sourceRecord.toDisplayString());
            return new SyncResult(1, List.of(sourceRecord.key()));
        }
        recordEvent("记录已最新，无需同步: " + sourceRecord.toDisplayString());
        return new SyncResult(0, List.of());
    }

    public SyncStatus currentStatus() {
        List<SyncEvent> eventsSnapshot = recentEvents.stream().toList();
        return new SyncStatus(lastSuccessfulSync, previewDifferences().size(), eventsSnapshot);
    }

    private boolean needsSync(SyncRecord sourceRecord) {
        return targetDataStore.findByKey(sourceRecord.key())
            .map(targetRecord -> targetRecord.lastModified().isBefore(sourceRecord.lastModified())
                || !targetRecord.value().equals(sourceRecord.value()))
            .orElse(true);
    }

    private void recordEvent(String message) {
        SyncEvent event = new SyncEvent(clock.instant(), message);
        recentEvents.addFirst(event);
        while (recentEvents.size() > MAX_RECENT_EVENTS) {
            recentEvents.removeLast();
        }
    }
}
