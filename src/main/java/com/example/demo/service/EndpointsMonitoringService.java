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
import com.example.demo.exception.UserNotAuthorized;
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
    protected final Map<Long, Instant> nextCheckTimes = new HashMap<>();
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

    public List<MonitoredEndpoint> getAllMonitoredEndpoints(String token) {
        User user = findUserByToken(token);
        return monitoredEndpointsRepository.findAllMonitoredEndpointsByUser(user.getId());
    }

    public MonitoredEndpoint getMonitoredEndpointById(Long id, String token) {
        var user = findUserByToken(token);
        var endpoint = findMonitoredEndpointById(id);

        if (!Objects.equals(endpoint.getUser().getId(), user.getId())) {
            throw new UserNotAuthorized("User doesn't own this endpoint");
        }

        return endpoint;
    }

    public List<MonitoringResult> resultsByEndpointId(Long id, String token){
        var user = findUserByToken(token);
        var endpoint = findMonitoredEndpointById(id);

        return monitoringResultRepository.findResultsByEndpointId(endpoint.getId(), user.getId());
    }

    public List<MonitoringResult> lastTenResultsByEndpointId(Long id, String token){
        var user = findUserByToken(token);
        var endpoint = findMonitoredEndpointById(id);

        return monitoringResultRepository.findLastTenResultsByEndpointId(endpoint.getId(), user.getId());
    }

    public MonitoredEndpoint createMonitoredEndpoint(MonitoredEndpointDTO newMonitoredEndpoint, String token) {
        var user = findUserByToken(token);
        var endpoint = buildMonitoredEndpoint(newMonitoredEndpoint, user);

        return monitoredEndpointsRepository.save(endpoint);
    }

    private MonitoredEndpoint buildMonitoredEndpoint(MonitoredEndpointDTO dto, User user) {
        MonitoredEndpoint endpoint = new MonitoredEndpoint();
        endpoint.setName(dto.getName());
        endpoint.setUrl(dto.getUrl());
        endpoint.setDateOfCreation(dto.getDateOfCreation() != null ? dto.getDateOfCreation() : LocalDateTime.now());
        endpoint.setDateOfLastCheck(dto.getDateOfLastCheck() != null ? dto.getDateOfLastCheck() : LocalDateTime.now());
        endpoint.setMonitoredInterval(dto.getMonitoredInterval());
        endpoint.setUser(user);

        return endpoint;
    }

    public MonitoredEndpoint updateMonitoredEndpoint(Long id, MonitoredEndpointDTO monitoredEndpointDetails, String token) {
        var user = findUserByToken(token);
        var endpoint = findMonitoredEndpointById(id);

        if (!Objects.equals(endpoint.getUser().getId(), user.getId())) {
            throw new UserNotAuthorized("User doesn't own this endpoint");
        }

        Optional.ofNullable(monitoredEndpointDetails.getName()).ifPresent(endpoint::setName);
        Optional.ofNullable(monitoredEndpointDetails.getUrl()).ifPresent(endpoint::setUrl);
        Optional.ofNullable(monitoredEndpointDetails.getDateOfCreation()).ifPresent(endpoint::setDateOfCreation);
        Optional.ofNullable(monitoredEndpointDetails.getDateOfLastCheck()).ifPresent(endpoint::setDateOfLastCheck);
        Optional.ofNullable(monitoredEndpointDetails.getMonitoredInterval()).ifPresent(endpoint::setMonitoredInterval);

        if (monitoredEndpointDetails.getUserId() != null) {
            User newUser = findUserById(monitoredEndpointDetails.getUserId());
            endpoint.setUser(newUser);
        }

        return monitoredEndpointsRepository.save(endpoint);
    }

    public void deleteMonitoredEndpoint(Long id, String token) {
        var user = findUserByToken(token);
        var endpoint = findMonitoredEndpointById(id);

        if (!Objects.equals(endpoint.getUser().getId(), user.getId())) {
            throw new UserNotAuthorized("User doesn't own this endpoint");
        }

        if (monitoredEndpointsRepository.existsById(id)) {
            monitoredEndpointsRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("MonitoredEndpoint not found with ID: " + id);
        }
    }

    private MonitoredEndpoint findMonitoredEndpointById(Long id) {
        return monitoredEndpointsRepository.findById(id)
                .orElseThrow(() -> new NoSuchMonitoredEndpointException("MonitoredEndpoint not found with ID: " + id));
    }

    private User findUserByToken(String token) {
        return userRepository.findUserByToken(UUID.fromString(token))
                .orElseThrow(() -> new NoSuchUserException("User not found with token: " + token));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User not found with ID: " + id));
    }

    @PostConstruct
    public void init() {
        List<MonitoredEndpoint> endpoints = monitoredEndpointsRepository.findAll();
        for (MonitoredEndpoint endpoint : endpoints) {
            nextCheckTimes.put(endpoint.getId(), Instant.now().plusSeconds(endpoint.getMonitoredInterval()));
        }
        scheduledExecutorService.scheduleAtFixedRate(this::runMonitoringTasks, 0, 1, TimeUnit.SECONDS);
    }

    protected void runMonitoringTasks() {
        Instant now = Instant.now();
        for (MonitoredEndpoint endpoint : monitoredEndpointsRepository.findAll()) {
            if (nextCheckTimes.get(endpoint.getId()).isBefore(now)) {
                checkAndLogEndpoint(endpoint);
                nextCheckTimes.put(endpoint.getId(), now.plusSeconds(endpoint.getMonitoredInterval()));
            }
        }
    }

    protected void checkAndLogEndpoint(MonitoredEndpoint endpoint) {
        var monitoringResult = new MonitoringResult();
        var timestamp = LocalDateTime.now();

        try {
            ResponseEntity<String> response = performHttpRequest(endpoint.getUrl());

            log.info("Monitored URL: {}", endpoint.getName());
            log.info("Status Code: {}", response.getStatusCode().value());
            log.info("Payload: {}", response.getBody());

            updateMonitoringResult(monitoringResult, response);

        } catch (HttpClientErrorException e) {
            log.warn("Error monitoring URL: {}, Status Code: {}, Error: {}", endpoint.getName(), e.getStatusCode(), e.getResponseBodyAsString());
            updateMonitoringResult(monitoringResult, e);

        } catch (Exception e) {
            log.error("Unexpected error when monitoring URL: {}. Error: {}", endpoint.getName(), e.getMessage());
            updateMonitoringResultForUnexpectedError(monitoringResult, e);

        } finally {
            finalizeMonitoring(monitoringResult, endpoint, timestamp);
        }
    }

    private ResponseEntity<String> performHttpRequest(String url) {
        return restTemplate.getForEntity(url, String.class);
    }

    private void updateMonitoringResult(MonitoringResult monitoringResult, ResponseEntity<String> response) {
        monitoringResult.setReturnedHttpStatusCode(response.getStatusCode().value());
        monitoringResult.setPayload(response.getBody());
    }

    private void updateMonitoringResult(MonitoringResult monitoringResult, HttpClientErrorException e) {
        monitoringResult.setReturnedHttpStatusCode(e.getStatusCode().value());
        monitoringResult.setPayload(e.getResponseBodyAsString());
    }

    private void updateMonitoringResultForUnexpectedError(MonitoringResult monitoringResult, Exception e) {
        monitoringResult.setReturnedHttpStatusCode(0);
        monitoringResult.setPayload("Unexpected error: " + e.getMessage());
    }

    private void finalizeMonitoring(MonitoringResult monitoringResult, MonitoredEndpoint endpoint, LocalDateTime timestamp) {
        monitoringResult.setDate(timestamp);
        endpoint.setDateOfLastCheck(timestamp);

        monitoringResult.setMonitoredEndpoint(endpoint);

        monitoringResultRepository.save(monitoringResult);
        monitoredEndpointsRepository.save(endpoint);
    }
}
