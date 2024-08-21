package com.example.demo.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MonitoredEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private LocalDateTime dateOfCreation;
    private LocalDateTime dateOfLastCheck;
    private Integer monitoredInterval;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public MonitoredEndpoint() {}

    public MonitoredEndpoint(
            String name,
            String url,
            LocalDateTime dateOfCreation,
            LocalDateTime dateOfLastCheck,
            Integer monitoredInterval,
            User user
    ) {
        this.name = name;
        this.url = url;
        this.dateOfCreation = dateOfCreation;
        this.dateOfLastCheck = dateOfLastCheck;
        this.monitoredInterval = monitoredInterval;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
