package com.dataprocessor.repository;

import com.dataprocessor.entity.WorkChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkChunkRepository extends JpaRepository<WorkChunk, Long> {
}
