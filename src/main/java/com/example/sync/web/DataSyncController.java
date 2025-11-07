package com.example.sync.web;

import com.example.sync.model.SyncRecord;
import com.example.sync.model.SyncResult;
import com.example.sync.model.SyncStatus;
import com.example.sync.service.DataSyncService;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class DataSyncController {

    private final DataSyncService dataSyncService;

    public DataSyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    @GetMapping("/preview")
    public List<SyncRecord> preview() {
        return dataSyncService.previewDifferences();
    }

    @PostMapping
    public SyncResult synchronizeAll() {
        return dataSyncService.syncAll();
    }

    @PostMapping("/{key}")
    public SyncResult synchronizeOne(@PathVariable String key) {
        return dataSyncService.syncRecord(key);
    }

    @GetMapping("/status")
    public SyncStatus status() {
        return dataSyncService.currentStatus();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
