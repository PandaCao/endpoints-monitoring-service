package com.example.demo.config;

import com.example.demo.domain.entity.MonitoredEndpoint;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.MonitoredEndpointsRepository;
import com.example.demo.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
public class LoadDatabase {

    private final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final MonitoredEndpointsRepository monitoredEndpointsRepository;

    @Autowired
    public LoadDatabase(JdbcTemplate jdbcTemplate, UserRepository userRepository, MonitoredEndpointsRepository monitoredEndpointsRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.monitoredEndpointsRepository = monitoredEndpointsRepository;
    }

    public void resetDB() {
        try {
            // Delete all records from the tables and reset auto-increment counters
            jdbcTemplate.execute("DELETE FROM monitoring_result");
            jdbcTemplate.execute("DELETE FROM monitored_endpoint");
            jdbcTemplate.execute("DELETE FROM user");

            jdbcTemplate.execute("ALTER TABLE user AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE monitored_endpoint AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE monitoring_result AUTO_INCREMENT = 1");

            log.info("Database reset completed. Auto-increment values set to 1.");
        } catch (Exception e) {
            log.error("Error resetting the database.", e);
        }
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Reset database
            resetDB();

            // Preload data
            User user1 = new User("Applifting", "info@applifting.cz", UUID.fromString("93f39e2f-80de-4033-99ee-249d92736a25"));
            User user2 = new User("Batman", "batman@example.com", UUID.fromString("dcb20f8a-5657-4f1b-9f7f-ce65739b359e"));

            MonitoredEndpoint e0 = new MonitoredEndpoint("pokemon0", "https://pokeapi.co/api/v2/pokemon?limit=1&offset=0", LocalDateTime.now(), LocalDateTime.now(), 2, user1);
            MonitoredEndpoint e1 = new MonitoredEndpoint("pokemon1", "https://pokeapi.co/api/v2/pokemon?limit=1&offset=1", LocalDateTime.now(), LocalDateTime.now(), 4, user1);
            MonitoredEndpoint e2 = new MonitoredEndpoint("pokemon2", "https://pokeapi.co/api/v2/pokemon?limit=1&offset=2", LocalDateTime.now(), LocalDateTime.now(), 10, user2);

            log.info("Preloading {}", userRepository.save(user1));
            log.info("Preloading {}", userRepository.save(user2));
            log.info("Preloading {}", monitoredEndpointsRepository.save(e0));
            log.info("Preloading {}", monitoredEndpointsRepository.save(e1));
            log.info("Preloading {}", monitoredEndpointsRepository.save(e2));
        };
    }
}
