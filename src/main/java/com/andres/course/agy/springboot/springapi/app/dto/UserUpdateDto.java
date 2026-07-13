package com.andres.course.agy.springboot.springapi.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email,

    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password, // Optional, can be null or empty, but if provided must be min 6 characters

    String roles // e.g. "ROLE_USER" or "ROLE_USER,ROLE_ADMIN"
) {}
