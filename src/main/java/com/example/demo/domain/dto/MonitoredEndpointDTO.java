package com.example.demo.domain.dto;

import java.time.LocalDateTime;

public class MonitoredEndpointDTO {
    private String name;
    private String url;
    private LocalDateTime dateOfCreation;
    private LocalDateTime dateOfLastCheck;
    private Integer monitoredInterval;
    private Integer userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDateTime dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public LocalDateTime getDateOfLastCheck() {
        return dateOfLastCheck;
    }

    public void setDateOfLastCheck(LocalDateTime dateOfLastCheck) {
        this.dateOfLastCheck = dateOfLastCheck;
    }

    public Integer getMonitoredInterval() {
        return monitoredInterval;
    }

    public void setMonitoredInterval(Integer monitoredInterval) {
        this.monitoredInterval = monitoredInterval;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer owner) {
        this.userId = owner;
    }
}
