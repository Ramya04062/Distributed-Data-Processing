
package com.dataprocessor.service;

import com.dataprocessor.enums.WorkerState;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WorkerManager {

    private final ThreadPoolExecutor executorService;

    private final Map<String, WorkerState> workerStates =
            new ConcurrentHashMap<>();

    public WorkerManager(
            @Value("${app.worker.pool-size:4}") int poolSize) {

        // Generate thread names
        AtomicInteger threadIndex = new AtomicInteger(1);

        ThreadFactory threadFactory = runnable -> {

            String workerId =
                    "data-worker-" + threadIndex.getAndIncrement();

            Thread thread = new Thread(runnable, workerId);

            thread.setDaemon(false);

            // Register the actual Java thread name and initial state
            workerStates.put(workerId, WorkerState.IDLE);

            return thread;
        };

        // Create fixed thread pool
        this.executorService = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory
        );

        // Start all core threads
        this.executorService.prestartAllCoreThreads();
    }

    // Get all currently idle workers
    public List<String> getIdleWorkers() {

        List<String> idleWorkers = new ArrayList<>();

        workerStates.forEach((workerId, state) -> {
            if (state == WorkerState.IDLE) {
                idleWorkers.add(workerId);
            }
        });

        return idleWorkers;
    }

    // Update worker runtime state
    public void updateWorkerState(String workerId, WorkerState state) {
        workerStates.put(workerId, state);
    }

    // Get all worker states
    public Map<String, WorkerState> getAllWorkerStates() {
        return Map.copyOf(workerStates);
    }

}
