package com.example.demo.controller;

import com.example.demo.domain.dto.MonitoredEndpoint;
import com.example.demo.domain.repository.MonitoredEndpointRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MonitoredEndpointController {
    private final MonitoredEndpointRepository monitoredEndpointRepository;

    public MonitoredEndpointController(MonitoredEndpointRepository monitoredEndpointRepository) {
        this.monitoredEndpointRepository = monitoredEndpointRepository;
    }

    @GetMapping
    public List<MonitoredEndpoint> getAllMonitoredEndpoints() {
        return monitoredEndpointRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoredEndpoint> getMonitoredEntityById(@PathVariable Long id) {
        return monitoredEndpointRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MonitoredEndpoint createMonitoredEndpoint(@RequestBody MonitoredEndpoint monitoredEndpoint) {
        return monitoredEndpointRepository.save(monitoredEndpoint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonitoredEndpoint> updateMonitoredEndpoint(@PathVariable Long id, @RequestBody MonitoredEndpoint monitoredEndpointDetails) {
        return ResponseEntity.ok(
                monitoredEndpointRepository.findById(id).map(endpoint -> {
                    endpoint.setName(monitoredEndpointDetails.getName());
                    endpoint.setUrl(monitoredEndpointDetails.getUrl());
                    return monitoredEndpointRepository.save(endpoint);
                }).orElseThrow(() -> new RuntimeException("User not found"))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MonitoredEndpoint> deleteMonitoredEndpoint(@PathVariable Long id) {
        monitoredEndpointRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
