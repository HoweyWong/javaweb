package com.example.sync.service;

import com.example.sync.config.SyncProperties;
import com.example.sync.model.SyncResult;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DataSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(DataSyncScheduler.class);

    private final DataSyncService dataSyncService;
    private final SyncProperties syncProperties;

    public DataSyncScheduler(DataSyncService dataSyncService, SyncProperties syncProperties) {
        this.dataSyncService = dataSyncService;
        this.syncProperties = syncProperties;
    }

    @Scheduled(fixedDelayString = "${sync.scheduling.fixed-delay:30s}")
    public void runScheduledSync() {
        if (!syncProperties.getScheduling().isEnabled()) {
            return;
        }
        SyncResult result = dataSyncService.syncAll();
        if (result.synchronizedCount() > 0) {
            log.info("Scheduled sync applied {} updates", result.synchronizedCount());
        } else {
            log.debug("Scheduled sync found no pending updates");
        }
    }

    public Duration configuredDelay() {
        return syncProperties.getScheduling().getFixedDelay();
    }
}
