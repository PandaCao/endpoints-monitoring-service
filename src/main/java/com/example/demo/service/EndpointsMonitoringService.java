package com.example.demo.service;

import com.example.demo.domain.dto.MonitoredEndpoint;
import com.example.demo.domain.dto.MonitoringResult;
import com.example.demo.domain.repository.MonitoredEndpointRepository;
import com.example.demo.domain.repository.MonitoringResultRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EndpointsMonitoringService {
    private final Logger LOG = Logger.getLogger(EndpointsMonitoringService.class.getName());

    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final MonitoringResultRepository monitoringResultRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public EndpointsMonitoringService(
            MonitoredEndpointRepository monitoredEndpointRepository,
            MonitoringResultRepository monitoringResultRepository
            ) {
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.monitoringResultRepository = monitoringResultRepository;
    }

    @Scheduled(fixedRate = 5000)  // Check all endpoints every 5 seconds
    public void monitorEndpoints() {
        List<MonitoredEndpoint> endpoints = monitoredEndpointRepository.findAll();

        for (MonitoredEndpoint endpoint : endpoints) {
            if (shouldCheckEndpoint(endpoint)) {
                var monitoringResult = new MonitoringResult();

                // Send an HTTP GET request to the URL
                ResponseEntity<String> response = restTemplate.getForEntity(endpoint.getUrl(), String.class);

                // Set monitoring result
                monitoringResult.setDate(LocalDateTime.now());
                monitoringResult.setReturnedHttpStatusCode(response.getStatusCode().value());
                monitoringResult.setPayload(response.getBody());
                monitoringResult.setMonitoredEndpoint(endpoint);

                // Log the information
                LOG.info("Monitored URL: " + endpoint.getUrl());
                LOG.info("Status Code: " + response.getStatusCode().value());
                LOG.info("Payload: " + response.getBody());

                // Save monitoring result
                monitoringResultRepository.save(monitoringResult);
            }
        }
    }

    private boolean shouldCheckEndpoint(MonitoredEndpoint endpoint) {
        LocalDateTime lastCheck = endpoint.getDateOfLastCheck();
        return lastCheck == null || lastCheck.plusSeconds(endpoint.getMonitoredInterval()).isBefore(LocalDateTime.now());
    }
}
