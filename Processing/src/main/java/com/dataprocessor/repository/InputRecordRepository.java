package com.dataprocessor.repository;

import com.dataprocessor.entity.InputRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputRecordRepository extends JpaRepository<InputRecord, Long> {
}
