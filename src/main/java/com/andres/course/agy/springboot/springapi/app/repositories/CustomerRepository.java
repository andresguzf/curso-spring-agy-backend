package com.andres.course.agy.springboot.springapi.app.repositories;

import com.andres.course.agy.springboot.springapi.app.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find a customer by their email address.
     * 
     * @param email the email address of the customer
     * @return an Optional containing the found Customer, or empty if none found
     */
    Optional<Customer> findByEmail(String email);
}
