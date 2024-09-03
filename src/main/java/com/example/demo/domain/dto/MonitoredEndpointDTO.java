package com.example.demo.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Getter
@Setter
public class MonitoredEndpointDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "URL cannot be blank")
    @URL(message = "URL must be a valid URL")
    private String url;

    private LocalDateTime dateOfCreation;

    private LocalDateTime dateOfLastCheck;

    @NotNull(message = "Monitored interval cannot be null")
    @Positive(message = "Monitored interval must be a positive number")
    private Integer monitoredInterval;

    private Long userId;
}
