package com.example.demo.service;

import com.example.demo.domain.dto.MonitoredEndpointDTO;
import com.example.demo.domain.entity.MonitoredEndpoint;
import com.example.demo.domain.entity.MonitoringResult;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.MonitoredEndpointsRepository;
import com.example.demo.domain.repository.MonitoringResultRepository;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.exception.NoSuchMonitoredEndpointException;
import com.example.demo.exception.NoSuchUserException;
import com.example.demo.exception.UserNotAuthorized;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class EndpointsMonitoringServiceTest {

    @Mock
    private MonitoredEndpointsRepository monitoredEndpointsRepository;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EndpointsMonitoringService endpointsMonitoringService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        setupMockUser();
        setupMockEndpoint();
        setupMockMonitoringResult();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private void setupMockUser() {
        User user = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findUserByToken(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void setupMockEndpoint() {
        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        when(monitoredEndpointsRepository.findById(1L)).thenReturn(Optional.of(endpoint));
        when(monitoredEndpointsRepository.findAllMonitoredEndpointsByUser(1L)).thenReturn(Collections.singletonList(endpoint));
        when(monitoredEndpointsRepository.findAll()).thenReturn(Collections.singletonList(endpoint));
        when(monitoredEndpointsRepository.save(any(MonitoredEndpoint.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(monitoredEndpointsRepository.existsById(anyLong())).thenReturn(true);
    }

    private void setupMockMonitoringResult() {
        MonitoringResult result = createMonitoringResult();
        when(monitoringResultRepository.findAll()).thenReturn(Collections.singletonList(result));
        when(monitoringResultRepository.save(any(MonitoringResult.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Applifting");
        user.setEmail("info@applifting.cz");
        user.setAccessToken(UUID.fromString("93f39e2f-80de-4033-99ee-249d92736a25"));
        return user;
    }

    private MonitoredEndpoint createMonitoredEndpoint() {
        MonitoredEndpoint endpoint = new MonitoredEndpoint();
        endpoint.setId(1L);
        endpoint.setUser(createUser());
        endpoint.setUrl("https://pokeapi.co/api/v2/pokemon?limit=1&offset=0");
        endpoint.setName("Test Endpoint");
        endpoint.setMonitoredInterval(20);
        return endpoint;
    }

    private MonitoringResult createMonitoringResult() {
        MonitoringResult result = new MonitoringResult();
        result.setId(1L);
        return result;
    }

    @Test
    void testGetAllMonitoredEndpoints_UserExists() {
        String token = "93f39e2f-80de-4033-99ee-249d92736a25";
        List<MonitoredEndpoint> endpoints = endpointsMonitoringService.getAllMonitoredEndpoints(token);

        assertNotNull(endpoints);
        assertEquals(1, endpoints.size());
    }

    @Test
    void testGetAllMonitoredEndpoints_UserNotFound() {
        String token = UUID.randomUUID().toString();
        when(userRepository.findUserByToken(UUID.fromString(token))).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> endpointsMonitoringService.getAllMonitoredEndpoints(token));
    }

    @Test
    void testGetMonitoredEndpointById_EndpointExists() {
        Long endpointId = 1L;
        String token = UUID.randomUUID().toString();
        MonitoredEndpoint result = endpointsMonitoringService.getMonitoredEndpointById(endpointId, token);

        assertNotNull(result);
        assertEquals(endpointId, result.getId());
    }

    @Test
    void testGetMonitoredEndpointById_EndpointNotFound() {
        Long endpointId = 1L;
        String token = UUID.randomUUID().toString();
        when(monitoredEndpointsRepository.findById(endpointId)).thenReturn(Optional.empty());

        assertThrows(NoSuchMonitoredEndpointException.class, () -> endpointsMonitoringService.getMonitoredEndpointById(endpointId, token));
    }

    @Test
    void testGetMonitoredEndpointById_UserNotAuthorized() {
        Long endpointId = 1L;
        String token = UUID.randomUUID().toString();
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);

        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        endpoint.setUser(new User());

        when(userRepository.findUserByToken(any(UUID.class))).thenReturn(Optional.of(unauthorizedUser));
        when(monitoredEndpointsRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));

        assertThrows(UserNotAuthorized.class, () -> endpointsMonitoringService.getMonitoredEndpointById(endpointId, token));
    }

    @Test
    void testCreateMonitoredEndpoint_UserExists() {
        MonitoredEndpointDTO dto = new MonitoredEndpointDTO();
        dto.setName("Endpoint Name");
        dto.setUrl("https://pokeapi.co/api/v2/pokemon?limit=1&offset=0");
        dto.setUserId(1L);

        MonitoredEndpoint result = endpointsMonitoringService.createMonitoredEndpoint(dto, UUID.randomUUID().toString());

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getUrl(), result.getUrl());
        assertEquals(1L, result.getUser().getId());
    }

    @Test
    void testUpdateMonitoredEndpoint_UserNotFound() {
        Long endpointId = 1L;
        MonitoredEndpointDTO dto = new MonitoredEndpointDTO();
        dto.setName("Endpoint Name");
        dto.setUrl("https://pokeapi.co/api/v2/pokemon?limit=1&offset=0");
        dto.setUserId(1L);

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> endpointsMonitoringService.updateMonitoredEndpoint(endpointId, dto, UUID.randomUUID().toString()));
    }


    @Test
    void testUpdateMonitoredEndpoint_EndpointExists() {
        Long endpointId = 1L;
        MonitoredEndpointDTO dto = new MonitoredEndpointDTO();
        dto.setName("Endpoint Name");
        dto.setUrl("https://pokeapi.co/api/v2/pokemon?limit=1&offset=0");
        dto.setUserId(1L);

        MonitoredEndpoint result = endpointsMonitoringService.updateMonitoredEndpoint(endpointId, dto, UUID.randomUUID().toString());

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
    }

    @Test
    void testUpdateMonitoredEndpoint_EndpointNotFound() {
        Long endpointId = 4L;
        MonitoredEndpointDTO dto = new MonitoredEndpointDTO();
        when(monitoredEndpointsRepository.findById(endpointId)).thenReturn(Optional.empty());

        assertThrows(NoSuchMonitoredEndpointException.class, () -> endpointsMonitoringService.updateMonitoredEndpoint(endpointId, dto, UUID.randomUUID().toString()));
    }

    @Test
    void testDeleteMonitoredEndpoint() {
        Long endpointId = 1L;
        String token = UUID.randomUUID().toString();

        doNothing().when(monitoredEndpointsRepository).deleteById(endpointId);

        endpointsMonitoringService.deleteMonitoredEndpoint(endpointId, token);

        verify(monitoredEndpointsRepository, times(1)).deleteById(endpointId);
    }

    @Test
    void testDeleteMonitoredEndpoint_UserNotAuthorized() {
        Long endpointId = 1L;
        String token = UUID.randomUUID().toString();
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);

        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        endpoint.setUser(new User());

        when(userRepository.findUserByToken(any(UUID.class))).thenReturn(Optional.of(unauthorizedUser));
        when(monitoredEndpointsRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));

        assertThrows(UserNotAuthorized.class, () -> endpointsMonitoringService.deleteMonitoredEndpoint(endpointId, token));
    }

    @Test
    void testRunMonitoringTasks() {
        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        endpoint.setMonitoredInterval(60);

        MonitoringResult result = createMonitoringResult();

        Instant pastTime = Instant.now().minusSeconds(10);
        endpointsMonitoringService.nextCheckTimes.put(1L, pastTime);

        endpointsMonitoringService.runMonitoringTasks();

        verify(monitoredEndpointsRepository, times(1)).findAll();
        assertTrue(endpointsMonitoringService.nextCheckTimes.get(1L).isAfter(Instant.now()));
    }

    @Test
    void testCheckAndLogEndpoint_SuccessfulResponse() {
        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        ResponseEntity<String> responseEntity = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.getForEntity(endpoint.getUrl(), String.class)).thenReturn(responseEntity);

        endpointsMonitoringService.checkAndLogEndpoint(endpoint);

        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
        verify(monitoredEndpointsRepository, times(1)).save(endpoint);
        assertEquals(LocalDateTime.now().getDayOfMonth(), endpoint.getDateOfLastCheck().getDayOfMonth());
    }

    @Test
    void testCheckAndLogEndpoint_HttpClientErrorException() {
        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        when(restTemplate.getForEntity(endpoint.getUrl(), String.class)).thenThrow(exception);

        endpointsMonitoringService.checkAndLogEndpoint(endpoint);

        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
        verify(monitoredEndpointsRepository, times(1)).save(endpoint);
    }

    @Test
    void testCheckAndLogEndpoint_UnhandledException() {
        MonitoredEndpoint endpoint = createMonitoredEndpoint();
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(restTemplate.getForEntity(endpoint.getUrl(), String.class)).thenThrow(exception);

        endpointsMonitoringService.checkAndLogEndpoint(endpoint);

        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
        verify(monitoredEndpointsRepository, times(1)).save(endpoint);
    }
}
