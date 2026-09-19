package com.unb.digitalbanking.config;

import com.unb.digitalbanking.entity.AppUser;
import com.unb.digitalbanking.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoUserInitializer {

    @Bean
    CommandLineRunner initializeDemoUser(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${demo.user.username}") String username,
            @Value("${demo.user.password}") String password,
            @Value("${demo.user.role}") String role,
            @Value("${demo.user.customer-id}") Long customerId
    ) {

        return args -> {

            if (appUserRepository.existsByUsername(username)) {
                return;
            }

            AppUser user = new AppUser();

            user.setUsername(username);
            user.setPasswordHash(
                    passwordEncoder.encode(password)
            );
            user.setRole(role);
            user.setCustomerId(customerId);
            user.setStatus("ACTIVE");

            appUserRepository.save(user);
        };
    }
}