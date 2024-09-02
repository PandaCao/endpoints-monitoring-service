package com.example.demo.domain.repository;

import com.example.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
            value = "SELECT * FROM user r WHERE r.access_token=:token",
            nativeQuery = true)
    Optional<User> findUserByToken(@Param("token") UUID token);
}
