package com.dataprocessor.service;

import com.dataprocessor.enums.WorkerState;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WorkerManager {

    public static final List<String> WORKER_IDS = List.of("worker-1", "worker-2", "worker-3", "worker-4");

    private final ThreadPoolExecutor executorService;
    private final Map<String, WorkerState> workerStates = new ConcurrentHashMap<>();
    private final Map<String, Boolean> failureFlags = new ConcurrentHashMap<>();
    private final Map<String, Boolean> slowFlags = new ConcurrentHashMap<>();

    public WorkerManager() {
        for (String id : WORKER_IDS) {
            workerStates.put(id, WorkerState.IDLE);
            failureFlags.put(id, false);
            slowFlags.put(id, false);
        }

        AtomicInteger threadIndex = new AtomicInteger(1);
        ThreadFactory customThreadFactory = runnable -> {
            int index = threadIndex.getAndIncrement();
            String name = (index <= 4) ? "worker-" + index : "worker-extra-" + index;
            Thread thread = new Thread(runnable, name);
            thread.setDaemon(false);
            return thread;
        };

        this.executorService = new ThreadPoolExecutor(
                4,
                4,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                customThreadFactory
        );
        this.executorService.prestartAllCoreThreads();
    }


    public Map<String, WorkerState> getAllWorkerStates() {
        return Map.copyOf(workerStates);
    }

    public Map<String, String> getLiveThreadDetails() {
        Map<String, String> threadStatus = new ConcurrentHashMap<>();
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().startsWith("worker-")) {
                threadStatus.put(t.getName(), t.getState().toString() + " (isAlive=" + t.isAlive() + ")");
            }
        }
        return threadStatus;
    }


    public void resetWorkers() {
        for (String id : WORKER_IDS) {
            workerStates.put(id, WorkerState.IDLE);
            failureFlags.put(id, false);
            slowFlags.put(id, false);
        }
    }

}
