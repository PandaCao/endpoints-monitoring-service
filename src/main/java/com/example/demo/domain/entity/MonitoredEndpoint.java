package com.example.demo.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MonitoredEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "URL cannot be blank")
    @URL(message = "URL must be a valid URL")
    private String url;

    @NotNull(message = "Date of creation cannot be null")
    private LocalDateTime dateOfCreation;

    private LocalDateTime dateOfLastCheck;

    @NotNull(message = "Monitored interval cannot be null")
    @Positive(message = "Monitored interval must be a positive number")
    private Integer monitoredInterval;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

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
