package com.andres.course.agy.springboot.springapi.app.services;

import com.andres.course.agy.springboot.springapi.app.dto.UserDto;
import com.andres.course.agy.springboot.springapi.app.dto.UserCreateDto;
import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto create(UserCreateDto userCreateDto);
    void delete(Long id);
}
