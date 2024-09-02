package com.example.demo.controller;

import com.example.demo.domain.dto.MonitoredEndpointDTO;
import com.example.demo.domain.entity.MonitoredEndpoint;
import com.example.demo.domain.entity.MonitoringResult;
import com.example.demo.service.EndpointsMonitoringService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/monitored-endpoints")
public class MonitoredEndpointsController {
    private final EndpointsMonitoringService endpointsMonitoringService;

    public MonitoredEndpointsController(EndpointsMonitoringService endpointsMonitoringService) {
        this.endpointsMonitoringService = endpointsMonitoringService;
    }

    @GetMapping
    public ResponseEntity<List<MonitoredEndpoint>> getAllMonitoredEndpoints(
            @RequestHeader("Authorization") String token
    ) {
        List<MonitoredEndpoint> endpoints = endpointsMonitoringService.getAllMonitoredEndpoints(token);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoredEndpoint> getMonitoredEndpointById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        MonitoredEndpoint endpoint = endpointsMonitoringService.getMonitoredEndpointById(id, token);
        return ResponseEntity.ok(endpoint);
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<List<MonitoringResult>> getResultsByMonitoredEndpointId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        List<MonitoringResult> results = endpointsMonitoringService.resultsByEndpointId(id, token);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/last-ten-results/{id}")
    public ResponseEntity<List<MonitoringResult>> getTenResultsByMonitoredEndpointId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        List<MonitoringResult> results = endpointsMonitoringService.lastTenResultsByEndpointId(id, token);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/create")
    public ResponseEntity<MonitoredEndpoint> createMonitoredEndpoint(
            @Valid @RequestBody MonitoredEndpointDTO monitoredEndpointDTO,
            @RequestHeader("Authorization") String token
    ) {
        try {
            MonitoredEndpoint endpoint = endpointsMonitoringService.createMonitoredEndpoint(monitoredEndpointDTO, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(endpoint);
        } catch (Exception e) {
            log.error("Error creating monitored endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MonitoredEndpoint> updateMonitoredEndpoint(
            @PathVariable Long id,
            @RequestBody MonitoredEndpointDTO monitoredEndpointDTO,
            @RequestHeader("Authorization") String token
    ) {
        MonitoredEndpoint updatedEndpoint = endpointsMonitoringService.updateMonitoredEndpoint(id, monitoredEndpointDTO, token);
        return ResponseEntity.ok(updatedEndpoint);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMonitoredEndpoint(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        endpointsMonitoringService.deleteMonitoredEndpoint(id, token);
        return ResponseEntity.noContent().build();
    }
}
