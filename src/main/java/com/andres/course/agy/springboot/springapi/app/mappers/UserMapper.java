package com.andres.course.agy.springboot.springapi.app.mappers;

import com.andres.course.agy.springboot.springapi.app.dto.UserDto;
import com.andres.course.agy.springboot.springapi.app.dto.UserCreateDto;
import com.andres.course.agy.springboot.springapi.app.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Map User Entity to UserDto.
     *
     * @param user the User entity
     * @return the UserDto record
     */
    public UserDto entityToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRoles()
        );
    }

    /**
     * Map UserCreateDto to User Entity.
     *
     * @param dto the UserCreateDto record
     * @return the User entity
     */
    public User createDtoToEntity(UserCreateDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        
        // Default to ROLE_USER if roles is null or blank
        if (dto.roles() == null || dto.roles().isBlank()) {
            user.setRoles("ROLE_USER");
        } else {
            user.setRoles(dto.roles());
        }
        return user;
    }
}
