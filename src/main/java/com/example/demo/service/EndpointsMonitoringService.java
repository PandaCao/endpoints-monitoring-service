package com.example.demo.service;

import com.example.demo.exception.NoSuchMonitoredEndpointException;
import com.example.demo.exception.NoSuchUserException;
import com.example.demo.domain.dto.MonitoredEndpointDTO;
import com.example.demo.domain.entity.MonitoredEndpoint;
import com.example.demo.domain.entity.MonitoringResult;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.MonitoredEndpointsRepository;
import com.example.demo.domain.repository.MonitoringResultRepository;
import com.example.demo.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EndpointsMonitoringService {
    private final Logger log = LoggerFactory.getLogger(EndpointsMonitoringService.class);

    private final MonitoredEndpointsRepository monitoredEndpointsRepository;
    private final MonitoringResultRepository monitoringResultRepository;
    private final ScheduledExecutorService scheduledExecutorService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<Long, Instant> nextCheckTimes = new HashMap<>();
    private final UserRepository userRepository;

    public EndpointsMonitoringService(
            MonitoredEndpointsRepository monitoredEndpointsRepository,
            MonitoringResultRepository monitoringResultRepository,
            UserRepository userRepository
    ) {
        this.monitoredEndpointsRepository = monitoredEndpointsRepository;
        this.monitoringResultRepository = monitoringResultRepository;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.userRepository = userRepository;
    }

    public List<MonitoredEndpoint> getAllMonitoredEndpoints() {
        return monitoredEndpointsRepository.findAll();
    }

    public Optional<MonitoredEndpoint> getMonitoredEndpointById(Long id) {
        return monitoredEndpointsRepository.findById(id);
    }

    public MonitoredEndpoint createMonitoredEndpoint(MonitoredEndpointDTO newMonitoredEndpoint) {
        var monitoredEndpoint = new MonitoredEndpoint();

        User user = userRepository.findById(newMonitoredEndpoint.getUserId().longValue())
                .orElseThrow(() -> new NoSuchUserException("User not found with ID: " + newMonitoredEndpoint.getUserId().longValue()));

        monitoredEndpoint.setName(newMonitoredEndpoint.getName());
        monitoredEndpoint.setUrl(newMonitoredEndpoint.getUrl());
        monitoredEndpoint.setDateOfCreation(newMonitoredEndpoint.getDateOfCreation());
        monitoredEndpoint.setDateOfLastCheck(newMonitoredEndpoint.getDateOfLastCheck());
        monitoredEndpoint.setMonitoredInterval(newMonitoredEndpoint.getMonitoredInterval());
        monitoredEndpoint.setUser(user);

        return monitoredEndpointsRepository.save(monitoredEndpoint);
    }

    public MonitoredEndpoint updateMonitoredEndpoint(Long id, MonitoredEndpointDTO monitoredEndpointDetails) {
        MonitoredEndpoint endpoint = monitoredEndpointsRepository.findById(id)
                .orElseThrow(() -> new NoSuchMonitoredEndpointException("MonitoredEndpoint not found with ID: " + id));

        // Updates only the provided fields
        if (monitoredEndpointDetails.getName() != null) {
            endpoint.setName(monitoredEndpointDetails.getName());
        }

        if (monitoredEndpointDetails.getUrl() != null) {
            endpoint.setUrl(monitoredEndpointDetails.getUrl());
        }

        if (monitoredEndpointDetails.getDateOfCreation() != null) {
            endpoint.setDateOfCreation(monitoredEndpointDetails.getDateOfCreation());
        }

        if (monitoredEndpointDetails.getDateOfLastCheck() != null) {
            endpoint.setDateOfLastCheck(monitoredEndpointDetails.getDateOfLastCheck());
        }

        if (monitoredEndpointDetails.getMonitoredInterval() != null) {
            endpoint.setMonitoredInterval(monitoredEndpointDetails.getMonitoredInterval());
        }

        if (monitoredEndpointDetails.getUserId() != null) {
            User newUser = userRepository.findById(monitoredEndpointDetails.getUserId().longValue())
                    .orElseThrow(() -> new NoSuchUserException("User not found with ID: " + monitoredEndpointDetails.getUserId().longValue()));
            endpoint.setUser(newUser);
        }

        return monitoredEndpointsRepository.save(endpoint);
    }

    public void deleteMonitoredEndpoint(Long id) {
        monitoredEndpointsRepository.deleteById(id);
    }

    @PostConstruct
    public void init() {
        List<MonitoredEndpoint> endpoints = monitoredEndpointsRepository.findAll();
        for (MonitoredEndpoint endpoint : endpoints) {
            nextCheckTimes.put(endpoint.getId(), Instant.now().plusSeconds(endpoint.getMonitoredInterval()));
        }
        scheduledExecutorService.scheduleAtFixedRate(this::runMonitoringTasks, 0, 1, TimeUnit.SECONDS);
    }

    private void runMonitoringTasks() {
        Instant now = Instant.now();
        for (MonitoredEndpoint endpoint : monitoredEndpointsRepository.findAll()) {
            if (nextCheckTimes.get(endpoint.getId()).isBefore(now)) {
                checkAndLogEndpoint(endpoint);
                nextCheckTimes.put(endpoint.getId(), now.plusSeconds(endpoint.getMonitoredInterval()));
            }
        }
    }

    private void checkAndLogEndpoint(MonitoredEndpoint endpoint) {
        var monitoringResult = new MonitoringResult();
        var timestamp = LocalDateTime.now();
        try {
            // Send an HTTP GET request to the URL
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint.getUrl(), String.class);

            // Log the successful response
            log.info("Monitored URL: {}", endpoint.getName());
            log.info("Status Code: {}", response.getStatusCode().value());
            log.info("Payload: {}", response.getBody());

            // Set successful response details
            monitoringResult.setDate(timestamp);
            monitoringResult.setReturnedHttpStatusCode(response.getStatusCode().value());
            monitoringResult.setPayload(response.getBody());
            monitoringResult.setMonitoredEndpoint(endpoint);

        } catch (HttpClientErrorException e) {
            log.warn("Error monitoring URL: {}, Status Code: {}, Error: {}", endpoint.getName(), e.getStatusCode(), e.getResponseBodyAsString());
            monitoringResult.setDate(timestamp);
            monitoringResult.setReturnedHttpStatusCode(e.getStatusCode().value());
            monitoringResult.setPayload(e.getResponseBodyAsString());
            monitoringResult.setMonitoredEndpoint(endpoint);

        } catch (Exception e) {
            log.error("Unexpected error when monitoring URL: {}. Error: {}", endpoint.getName(), e.getMessage());
            monitoringResult.setDate(timestamp);
            monitoringResult.setReturnedHttpStatusCode(0);
            monitoringResult.setPayload("Unexpected error: " + e.getMessage());
            monitoringResult.setMonitoredEndpoint(endpoint);

        } finally {
            // Update the last check time
            endpoint.setDateOfLastCheck(timestamp);

            // Save monitoring result and endpoint
            monitoringResultRepository.save(monitoringResult);
            monitoredEndpointsRepository.save(endpoint);
        }

    }
}
