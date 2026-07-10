package com.andres.course.agy.springboot.springapi.app.dto;

public record UserDto(
    Long id,
    String email,
    String roles
) {}
