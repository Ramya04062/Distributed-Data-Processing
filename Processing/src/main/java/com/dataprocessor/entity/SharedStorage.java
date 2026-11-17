package com.dataprocessor.entity;

import com.dataprocessor.enums.ChunkStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
public class SharedStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private String workerId;

    @Column(name = "range_start", nullable = false)
    private Long rangeStart;

    @Column(name = "range_end", nullable = false)
    private Long rangeEnd;

    @Column(name = "offset", nullable = false)
    private Long offset;

    @Column(name = "fencing_token")
    private Long fencingToken;

    @Column(name = "lease_expiry")
    private LocalDateTime leaseExpiry;

    @Column(name = "heartbeat")
    private LocalDateTime heartbeat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChunkStatus status;

    public SharedStorage() {
    }

    public SharedStorage(String workerId, Long rangeStart, Long rangeEnd,
                         Long offset, Long fencingToken, LocalDateTime leaseExpiry,
                         LocalDateTime heartbeat, ChunkStatus status) {
        this.workerId = workerId;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.offset = offset;
        this.fencingToken = fencingToken;
        this.leaseExpiry = leaseExpiry;
        this.heartbeat = heartbeat;
        this.status = status;
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

    public Long getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(Long rangeStart) {
        this.rangeStart = rangeStart;
    }

    public Long getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(Long rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getFencingToken() {
        return fencingToken;
    }

    public void setFencingToken(Long fencingToken) {
        this.fencingToken = fencingToken;
    }

    public LocalDateTime getLeaseExpiry() {
        return leaseExpiry;
    }

    public void setLeaseExpiry(LocalDateTime leaseExpiry) {
        this.leaseExpiry = leaseExpiry;
    }

    public LocalDateTime getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(LocalDateTime heartbeat) {
        this.heartbeat = heartbeat;
    }

    public ChunkStatus getStatus() {
        return status;
    }

    public void setStatus(ChunkStatus status) {
        this.status = status;
    }
}
