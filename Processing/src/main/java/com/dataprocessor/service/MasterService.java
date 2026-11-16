package com.dataprocessor.service;

import com.dataprocessor.entity.InputRecord;
import com.dataprocessor.entity.WorkChunk;
import com.dataprocessor.enums.ChunkStatus;
import com.dataprocessor.repository.InputRecordRepository;
import com.dataprocessor.repository.ProcessedRecordRepository;
import com.dataprocessor.repository.WorkChunkRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MasterService {

    private final InputRecordRepository inputRecordRepository;
    private final ProcessedRecordRepository processedRecordRepository;
    private final WorkChunkRepository workChunkRepository;
    private final WorkerManager workerManager;

    public MasterService(InputRecordRepository inputRecordRepository,
                         ProcessedRecordRepository processedRecordRepository,
                         WorkChunkRepository workChunkRepository,
                         WorkerManager workerManager) {
        this.inputRecordRepository = inputRecordRepository;
        this.processedRecordRepository = processedRecordRepository;
        this.workChunkRepository = workChunkRepository;
        this.workerManager = workerManager;
    }

    public List<WorkChunk> startJob() {
        resetAndGenerate500Records();
        workChunkRepository.deleteAllInBatch();
        workerManager.resetWorkers();

        List<InputRecord> allRecords = inputRecordRepository.findAll(Sort.by("id"));
        int totalRecords = allRecords.size();
        int numWorkers = 4;
        int chunkSize = totalRecords / numWorkers;

        List<WorkChunk> assignedChunks = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            String workerId = "worker-" + (i + 1);
            int startIndex = i * chunkSize;
            int endIndex = (i == numWorkers - 1) ? totalRecords - 1 : (i + 1) * chunkSize - 1;

            long rangeStart = allRecords.get(startIndex).getId();
            long rangeEnd = allRecords.get(endIndex).getId();

            WorkChunk chunk = new WorkChunk(
                    workerId,
                    rangeStart,
                    rangeEnd,
                    rangeStart,
                    1L,
                    null,
                    null,
                    ChunkStatus.ASSIGNED
            );
            assignedChunks.add(chunk);
        }

        return workChunkRepository.saveAllAndFlush(assignedChunks);
    }

    private void resetAndGenerate500Records() {
        inputRecordRepository.deleteAllInBatch();
        List<InputRecord> records = new ArrayList<>(500);
        LocalDateTime now = LocalDateTime.now();

        for (long i = 1; i <= 500; i++) {
            records.add(new InputRecord(i, "Record Payload #" + i, "PENDING", now));
        }

        inputRecordRepository.saveAllAndFlush(records);
    }

    public Map<String, Object> getProgress() {
        long totalRecords = inputRecordRepository.count();
        long committedRecords = processedRecordRepository.count();
        double percentage = (totalRecords > 0) ? ((double) committedRecords / totalRecords) * 100.0 : 0.0;

        return Map.of(
                "totalRecords", totalRecords,
                "committedRecords", committedRecords,
                "percentage", Math.round(percentage * 100.0) / 100.0,
                "workerStates", workerManager.getAllWorkerStates(),
                "liveThreads", workerManager.getLiveThreadDetails(),
                "chunks", workChunkRepository.findAll()
        );
    }
}
