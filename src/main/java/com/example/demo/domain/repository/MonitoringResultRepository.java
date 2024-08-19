package com.example.demo.domain.repository;

import com.example.demo.domain.dto.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {
}
