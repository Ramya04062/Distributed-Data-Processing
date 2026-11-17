package com.dataprocessor.controller;

import com.dataprocessor.entity.SharedStorage;
import com.dataprocessor.enums.WorkerState;
import com.dataprocessor.service.MasterService;
import com.dataprocessor.service.WorkerManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JobController {

    private final MasterService masterService;
    private final WorkerManager workerManager;

    public JobController(MasterService masterService, WorkerManager workerManager) {
        this.masterService = masterService;
        this.workerManager = workerManager;
    }

    @PostMapping("/jobs/start")
    public ResponseEntity<Map<String, Object>> startJob() {
        List<SharedStorage> chunks = masterService.startJob();
        return ResponseEntity.ok(Map.of(
                "message", "Processing job started successfully",
                "chunksAssigned", chunks.size(),
                "chunks", chunks
        ));
    }

    @GetMapping("/workers")
    public ResponseEntity<Map<String, WorkerState>> getWorkerStatuses() {
        return ResponseEntity.ok(workerManager.getAllWorkerStates());
    }


}
