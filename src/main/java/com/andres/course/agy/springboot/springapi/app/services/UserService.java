package com.andres.course.agy.springboot.springapi.app.services;

import com.andres.course.agy.springboot.springapi.app.dto.UserDto;
import com.andres.course.agy.springboot.springapi.app.dto.UserCreateDto;
import com.andres.course.agy.springboot.springapi.app.dto.UserUpdateDto;
import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto create(UserCreateDto userCreateDto);
    UserDto update(Long id, UserUpdateDto userUpdateDto);
    void delete(Long id);
}
