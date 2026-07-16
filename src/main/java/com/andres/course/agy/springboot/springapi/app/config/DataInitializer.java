package com.andres.course.agy.springboot.springapi.app.config;

import com.andres.course.agy.springboot.springapi.app.models.Customer;
import com.andres.course.agy.springboot.springapi.app.models.User;
import com.andres.course.agy.springboot.springapi.app.repositories.CustomerRepository;
import com.andres.course.agy.springboot.springapi.app.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
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

        if (customerRepository.count() == 0) {
            // Seed 10 sample customers
            customerRepository.save(new Customer("John", "Doe", "john.doe@example.com", "123-456-7890", "123 Main St, Springfield"));
            customerRepository.save(new Customer("Jane", "Smith", "jane.smith@example.com", "234-567-8901", "456 Elm St, Shelbyville"));
            customerRepository.save(new Customer("Alice", "Johnson", "alice.johnson@example.com", "345-678-9012", "789 Oak St, Capital City"));
            customerRepository.save(new Customer("Bob", "Brown", "bob.brown@example.com", "456-789-0123", "321 Pine St, Ogdenville"));
            customerRepository.save(new Customer("Charlie", "Davis", "charlie.davis@example.com", "567-890-1234", "654 Maple St, North Haverbrook"));
            customerRepository.save(new Customer("Diana", "Miller", "diana.miller@example.com", "678-901-2345", "987 Cedar St, Brockway"));
            customerRepository.save(new Customer("Evan", "Wilson", "evan.wilson@example.com", "789-012-3456", "159 Birch St, Cypress Creek"));
            customerRepository.save(new Customer("Fiona", "Moore", "fiona.moore@example.com", "890-123-4567", "753 Walnut St, Terror Lake"));
            customerRepository.save(new Customer("George", "Taylor", "george.taylor@example.com", "901-234-5678", "951 Cherry St, Waverly Hills"));
            customerRepository.save(new Customer("Hannah", "Anderson", "hannah.anderson@example.com", "012-345-6789", "357 Ash St, Springfield"));

            System.out.println("--- Data Initialization Complete: 10 Customers Seeded ---");
        }
    }
}
