
package com.dataprocessor.service;

import com.dataprocessor.entity.InputRecord;
import com.dataprocessor.entity.SharedStorage;
import com.dataprocessor.enums.ChunkStatus;
import com.dataprocessor.enums.WorkerState;
import com.dataprocessor.repository.InputRecordRepository;
import com.dataprocessor.repository.WorkChunkRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MasterService {

    private final InputRecordRepository inputRecordRepository;
    private final WorkChunkRepository workChunkRepository;
    private final WorkerManager workerManager;

    public MasterService(
            InputRecordRepository inputRecordRepository,
            WorkChunkRepository workChunkRepository,
            WorkerManager workerManager) {

        this.inputRecordRepository = inputRecordRepository;
        this.workChunkRepository = workChunkRepository;
        this.workerManager = workerManager;
    }

    public synchronized List<SharedStorage> startJob() {

        System.out.println("working");

        // Generate input records
        resetAndGenerate500Records();

        // Remove previous assignments
        workChunkRepository.deleteAllInBatch();

        // Fetch all input records in ID order
        List<InputRecord> allRecords =
                inputRecordRepository.findAll(Sort.by("id"));

        int totalRecords = allRecords.size();

        if (totalRecords == 0) {
            return List.of();
        }

        // Get currently idle Java thread names
        List<String> idleWorkers = workerManager.getIdleWorkers();

        int numWorkers = idleWorkers.size();

        if (numWorkers == 0) {
            throw new IllegalStateException(
                    "No idle workers available"
            );
        }

        int chunkSize = totalRecords / numWorkers;

        List<SharedStorage> assignedChunks = new ArrayList<>();

        // Divide records among available workers
        for (int i = 0; i < numWorkers; i++) {

            String workerId = idleWorkers.get(i);

            int startIndex = i * chunkSize;

            int endIndex = (i == numWorkers - 1)
                    ? totalRecords - 1
                    : (i + 1) * chunkSize - 1;

            long rangeStart =
                    allRecords.get(startIndex).getId();

            long rangeEnd =
                    allRecords.get(endIndex).getId();

            SharedStorage chunk = new SharedStorage(
                    workerId,
                    rangeStart,
                    rangeEnd,
                    rangeStart,
                    null,
                    null,
                    null,
                    ChunkStatus.ASSIGNED
            );

            assignedChunks.add(chunk);
        }

        // Save assignments in MySQL
        List<SharedStorage> savedChunks =
                workChunkRepository.saveAllAndFlush(assignedChunks);

        // Mark assigned workers as BUSY
        for (SharedStorage chunk : savedChunks) {
            workerManager.updateWorkerState(
                    chunk.getWorkerId(),
                    WorkerState.BUSY
            );
        }

        return savedChunks;
    }

    private void resetAndGenerate500Records() {

        inputRecordRepository.deleteAllInBatch();

        List<InputRecord> records = new ArrayList<>(500);

        LocalDateTime now = LocalDateTime.now();

        for (long i = 1; i <= 500; i++) {

            records.add(
                    new InputRecord(
                            i,
                            "Record Payload #" + i,
                            "PENDING",
                            now
                    )
            );
        }

        inputRecordRepository.saveAllAndFlush(records);
    }
}
