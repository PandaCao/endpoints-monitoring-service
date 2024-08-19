package com.example.demo.domain.repository;

import com.example.demo.domain.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
