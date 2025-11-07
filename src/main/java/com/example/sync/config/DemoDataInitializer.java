package com.example.sync.config;

import com.example.sync.store.SourceDataStore;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final SourceDataStore sourceDataStore;
    private final SyncProperties syncProperties;

    public DemoDataInitializer(SourceDataStore sourceDataStore, SyncProperties syncProperties) {
        this.sourceDataStore = sourceDataStore;
        this.syncProperties = syncProperties;
    }

    @Override
    public void run(String... args) {
        if (!syncProperties.isDemoDataEnabled() || !sourceDataStore.isEmpty()) {
            return;
        }
        Map<String, String> demoRecords = new LinkedHashMap<>();
        demoRecords.put("customer-1001", "Alice Johnson");
        demoRecords.put("customer-1002", "Bob Smith");
        demoRecords.put("customer-1003", "Carlos Diaz");
        sourceDataStore.saveAll(demoRecords);
        log.info("Loaded {} demo records into the source store", demoRecords.size());
    }
}
