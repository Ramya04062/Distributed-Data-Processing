package com.dataprocessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_records")
public class ProcessedRecord {

    @Id
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private String workerId;

    @Column(name = "processed_data", nullable = false)
    private String processedData;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public ProcessedRecord() {
    }

    public ProcessedRecord(Long id, String workerId, String processedData, LocalDateTime processedAt) {
        this.id = id;
        this.workerId = workerId;
        this.processedData = processedData;
        this.processedAt = processedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getProcessedData() {
        return processedData;
    }

    public void setProcessedData(String processedData) {
        this.processedData = processedData;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
