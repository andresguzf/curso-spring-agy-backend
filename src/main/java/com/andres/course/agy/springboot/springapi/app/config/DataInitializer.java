package com.andres.course.agy.springboot.springapi.app.config;

import com.andres.course.agy.springboot.springapi.app.models.User;
import com.andres.course.agy.springboot.springapi.app.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Create Admin User
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles("ROLE_USER,ROLE_ADMIN");
            userRepository.save(admin);

            // Create Standard User
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles("ROLE_USER");
            userRepository.save(user);

            System.out.println("--- Data Initialization Complete: Users Created ---");
            System.out.println("Admin: admin@example.com / admin123");
            System.out.println("User: user@example.com / user123");
        }
    }
}
