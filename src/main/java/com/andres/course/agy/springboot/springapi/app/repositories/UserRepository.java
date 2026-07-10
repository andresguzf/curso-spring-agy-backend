package com.andres.course.agy.springboot.springapi.app.repositories;

import com.andres.course.agy.springboot.springapi.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by their email address.
     * 
     * @param email the email address of the user
     * @return an Optional containing the found User, or empty if none found
     */
    Optional<User> findByEmail(String email);
}
