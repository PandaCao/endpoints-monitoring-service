package com.example.demo.service;

import com.example.demo.domain.dto.MonitoredEndpoint;
import com.example.demo.domain.dto.MonitoringResult;
import com.example.demo.domain.repository.MonitoredEndpointsRepository;
import com.example.demo.domain.repository.MonitoringResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EndpointsMonitoringService {
    private final Logger log = LoggerFactory.getLogger(EndpointsMonitoringService.class);

    private final MonitoredEndpointsRepository monitoredEndpointsRepository;
    private final MonitoringResultRepository monitoringResultRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public EndpointsMonitoringService(
            MonitoredEndpointsRepository monitoredEndpointsRepository,
            MonitoringResultRepository monitoringResultRepository
            ) {
        this.monitoredEndpointsRepository = monitoredEndpointsRepository;
        this.monitoringResultRepository = monitoringResultRepository;
    }

    public List<MonitoredEndpoint> getAllMonitoredEndpoints() {
        return monitoredEndpointsRepository.findAll();
    }

    public Optional<MonitoredEndpoint> getMonitoredEndpointById(Long id) {
        return monitoredEndpointsRepository.findById(id);
    }

    public MonitoredEndpoint createMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        return monitoredEndpointsRepository.save(monitoredEndpoint);
    }

    public MonitoredEndpoint updateMonitoredEndpoint(Long id, MonitoredEndpoint monitoredEndpointDetails) {
        return monitoredEndpointsRepository.findById(id).map(endpoint -> {
            endpoint.setName(monitoredEndpointDetails.getName());
            endpoint.setUrl(monitoredEndpointDetails.getUrl());
            return monitoredEndpointsRepository.save(endpoint);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteMonitoredEndpoint(Long id) {
        monitoredEndpointsRepository.deleteById(id);
    }

    public int getMonitoredInterval(Long id){
        var endpoint = monitoredEndpointsRepository.findById(id);
        if (endpoint.isPresent())
            return endpoint.get().getMonitoredInterval();
        else{
            log.error("Monitored endpoint not found");
            return 0;
        }
    }

    //TODO update this logic to get interval from DB
    @Scheduled(fixedRate = 5000)  // Check all endpoints every 5 seconds
    public void monitorEndpoints() {
        List<MonitoredEndpoint> endpoints = monitoredEndpointsRepository.findAll();

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
                log.info("Monitored URL: {}", endpoint.getUrl());
                log.info("Status Code: {}", response.getStatusCode().value());
                log.info("Payload: {}", response.getBody());

                // Save monitoring result
                monitoringResultRepository.save(monitoringResult);
            }
        }

        log.info("{}", LocalDateTime.now());
    }

    private boolean shouldCheckEndpoint(MonitoredEndpoint endpoint) {
        LocalDateTime lastCheck = endpoint.getDateOfLastCheck();
        return lastCheck == null || lastCheck.plusSeconds(endpoint.getMonitoredInterval()).isBefore(LocalDateTime.now());
    }
}
