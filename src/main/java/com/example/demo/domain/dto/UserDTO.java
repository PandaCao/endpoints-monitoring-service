package com.example.demo.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String email;
    private UUID accessToken;
}
