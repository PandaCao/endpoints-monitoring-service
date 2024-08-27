package com.example.demo.domain.repository;

import com.example.demo.domain.entity.MonitoredEndpoint;
import com.example.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoredEndpointsRepository extends JpaRepository<MonitoredEndpoint, Long> {

    @Query(
            value = "SELECT * FROM monitored_endpoint m WHERE m.user_id=:user",
            nativeQuery = true)
    List<MonitoredEndpoint> findByUser(@Param("user") User user);
}
