package com.dataprocessor.repository;

import com.dataprocessor.entity.SharedStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkChunkRepository extends JpaRepository<SharedStorage, Long> {
}
