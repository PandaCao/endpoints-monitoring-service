package com.example.demo.domain.repository;

import com.example.demo.domain.entity.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoredEndpointsRepository extends JpaRepository<MonitoredEndpoint, Long> {
}
