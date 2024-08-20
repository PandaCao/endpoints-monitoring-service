package com.example.demo.domain.dto;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MonitoringResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private Integer returnedHttpStatusCode;
    private String payload;
    @ManyToOne
    @JoinColumn(name = "monitored_endpoint_id")
    private MonitoredEndpoint monitoredEndpoint;

    public Long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getReturnedHttpStatusCode() {
        return returnedHttpStatusCode;
    }

    public void setReturnedHttpStatusCode(Integer returnedHttpStatusCode) {
        this.returnedHttpStatusCode = returnedHttpStatusCode;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MonitoredEndpoint getMonitoredEndpoint() {
        return monitoredEndpoint;
    }

    public void setMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        this.monitoredEndpoint = monitoredEndpoint;
    }
}
