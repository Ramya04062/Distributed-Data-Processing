package com.dataprocessor.repository;

import com.dataprocessor.entity.ProcessedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedRecordRepository extends JpaRepository<ProcessedRecord, Long> {
}
