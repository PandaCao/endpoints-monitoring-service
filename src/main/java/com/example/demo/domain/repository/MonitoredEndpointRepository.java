package com.example.demo.domain.repository;

import com.example.demo.domain.dto.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, Long> {
}
