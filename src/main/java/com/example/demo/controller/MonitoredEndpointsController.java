package com.example.demo.controller;

import com.example.demo.domain.dto.MonitoredEndpointDTO;
import com.example.demo.domain.entity.MonitoredEndpoint;
import com.example.demo.domain.entity.MonitoringResult;
import com.example.demo.domain.repository.MonitoringResultRepository;
import com.example.demo.service.EndpointsMonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MonitoredEndpointsController {
    private final EndpointsMonitoringService endpointsMonitoringService;
    private final MonitoringResultRepository monitoringResultRepository;

    public MonitoredEndpointsController(EndpointsMonitoringService endpointsMonitoringService, MonitoringResultRepository monitoringResultRepository) {
        this.endpointsMonitoringService = endpointsMonitoringService;
        this.monitoringResultRepository = monitoringResultRepository;
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

    @GetMapping("/monitored-endpoints/last-ten-results/{id}")
    public List<MonitoringResult> getLastTenResultsByEndpointId(@PathVariable Long id) {
        return monitoringResultRepository.findLastTenResultsByEndpointId(id);
    }

    @PostMapping("/monitored-endpoints/create")
    public MonitoredEndpoint createMonitoredEndpoint(@RequestBody MonitoredEndpointDTO monitoredEndpoint) {
        return endpointsMonitoringService.createMonitoredEndpoint(monitoredEndpoint);
    }

    @PutMapping("/monitored-endpoints/update/{id}")
    public ResponseEntity<MonitoredEndpoint> updateMonitoredEndpoint(@PathVariable Long id, @RequestBody MonitoredEndpointDTO monitoredEndpointDetails) {
        return ResponseEntity.ok(endpointsMonitoringService.updateMonitoredEndpoint(id, monitoredEndpointDetails));
    }

    @DeleteMapping("/monitored-endpoints/delete/{id}")
    public ResponseEntity<MonitoredEndpoint> deleteMonitoredEndpoint(@PathVariable Long id) {
        endpointsMonitoringService.deleteMonitoredEndpoint(id);
        return ResponseEntity.noContent().build();
    }
}
