package com.example.sync;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.sync.model.SyncRecord;
import com.example.sync.model.SyncResult;
import com.example.sync.model.SyncStatus;
import com.example.sync.service.DataSyncService;
import com.example.sync.store.SourceDataStore;
import com.example.sync.store.TargetDataStore;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataSyncServiceTest {

    private Clock fixedClock;
    private SourceDataStore sourceDataStore;
    private TargetDataStore targetDataStore;
    private DataSyncService dataSyncService;

    @BeforeEach
    void setUp() {
        Instant baseInstant = Instant.parse("2024-01-01T10:15:30Z");
        fixedClock = Clock.fixed(baseInstant, ZoneOffset.UTC);
        sourceDataStore = new SourceDataStore(fixedClock);
        targetDataStore = new TargetDataStore();
        dataSyncService = new DataSyncService(sourceDataStore, targetDataStore, fixedClock);

        sourceDataStore.save("customer-1", "Alice");
        sourceDataStore.save("customer-2", "Bob");
    }

    @Test
    void previewDifferences_shouldReturnRecordsMissingFromTarget() {
        List<String> keys = dataSyncService.previewDifferences().stream()
            .map(SyncRecord::key)
            .toList();
        assertThat(keys).containsExactlyInAnyOrder("customer-1", "customer-2");
    }

    @Test
    void syncAll_shouldPopulateTargetStore() {
        SyncResult result = dataSyncService.syncAll();
        assertThat(result.synchronizedCount()).isEqualTo(2);
        assertThat(targetDataStore.findByKey("customer-1")).isPresent();
        assertThat(targetDataStore.findByKey("customer-2")).isPresent();
    }

    @Test
    void syncRecord_shouldReturnZeroWhenRecordIsUpToDate() {
        dataSyncService.syncAll();

        SyncResult result = dataSyncService.syncRecord("customer-1");

        assertThat(result.synchronizedCount()).isZero();
    }

    @Test
    void syncRecord_shouldUpdateStaleRecord() {
        dataSyncService.syncAll();

        SyncRecord updated = new SyncRecord("customer-2", "Bob Updated",
            fixedClock.instant().plusSeconds(60));
        sourceDataStore.save(updated);

        SyncResult result = dataSyncService.syncRecord("customer-2");

        assertThat(result.synchronizedCount()).isEqualTo(1);
        assertThat(targetDataStore.findByKey("customer-2"))
            .get()
            .extracting(SyncRecord::value)
            .isEqualTo("Bob Updated");
    }

    @Test
    void currentStatus_shouldReflectPendingRecords() {
        SyncStatus status = dataSyncService.currentStatus();
        assertThat(status.pendingRecords()).isEqualTo(2);
        assertThat(status.recentEvents()).isEmpty();
    }
}
