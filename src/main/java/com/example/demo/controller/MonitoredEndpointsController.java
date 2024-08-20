package com.example.demo.controller;

import com.example.demo.domain.dto.MonitoredEndpoint;
import com.example.demo.service.EndpointsMonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MonitoredEndpointsController {
    private final EndpointsMonitoringService endpointsMonitoringService;

    public MonitoredEndpointsController(EndpointsMonitoringService endpointsMonitoringService) {
        this.endpointsMonitoringService = endpointsMonitoringService;
    }

    @GetMapping("/monitored-endpoints")
    public List<MonitoredEndpoint> getAllMonitoredEndpoints() {
        return endpointsMonitoringService.getAllMonitoredEndpoints();
    }

    @GetMapping("/monitored-endpoints/{id}")
    public ResponseEntity<MonitoredEndpoint> getMonitoredEntityById(@PathVariable Long id) {
        return endpointsMonitoringService.getMonitoredEndpointById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/monitored-endpoints")
    public MonitoredEndpoint createMonitoredEndpoint(@RequestBody MonitoredEndpoint monitoredEndpoint) {
        return endpointsMonitoringService.createMonitoredEndpoint(monitoredEndpoint);
    }

    @PutMapping("/monitored-endpoints/{id}")
    public ResponseEntity<MonitoredEndpoint> updateMonitoredEndpoint(@PathVariable Long id, @RequestBody MonitoredEndpoint monitoredEndpointDetails) {
        return ResponseEntity.ok(endpointsMonitoringService.updateMonitoredEndpoint(id, monitoredEndpointDetails));
    }

    @DeleteMapping("/monitored-endpoints/{id}")
    public ResponseEntity<MonitoredEndpoint> deleteMonitoredEndpoint(@PathVariable Long id) {
        endpointsMonitoringService.deleteMonitoredEndpoint(id);
        return ResponseEntity.noContent().build();
    }
}
