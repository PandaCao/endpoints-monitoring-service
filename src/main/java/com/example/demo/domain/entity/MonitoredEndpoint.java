package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MonitoredEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
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
}
