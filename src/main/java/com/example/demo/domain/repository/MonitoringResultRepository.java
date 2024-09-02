package com.example.demo.domain.repository;

import com.example.demo.domain.entity.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {

    @Query(
            value = "SELECT * FROM monitoring_result r WHERE r.monitored_endpoint_id=:endpointId AND r.monitored_endpoint_user_id=:userId ORDER BY r.date DESC LIMIT 10",
            nativeQuery = true)
    List<MonitoringResult> findLastTenResultsByEndpointId(@Param("endpointId") Long endpointI, @Param("userId") Long userId);

    @Query(
            value = "SELECT * FROM monitoring_result r WHERE r.monitored_endpoint_id=:endpointId AND r.monitored_endpoint_user_id=:userId ORDER BY r.date",
            nativeQuery = true)
    List<MonitoringResult> findResultsByEndpointId(@Param("endpointId") Long endpointId, @Param("userId") Long userId);
}
