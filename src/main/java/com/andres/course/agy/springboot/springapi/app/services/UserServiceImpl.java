package com.andres.course.agy.springboot.springapi.app.services;

import com.andres.course.agy.springboot.springapi.app.dto.UserDto;
import com.andres.course.agy.springboot.springapi.app.dto.UserCreateDto;
import com.andres.course.agy.springboot.springapi.app.dto.UserUpdateDto;
import com.andres.course.agy.springboot.springapi.app.exceptions.EmailAlreadyExistsException;
import com.andres.course.agy.springboot.springapi.app.mappers.UserMapper;
import com.andres.course.agy.springboot.springapi.app.models.User;
import com.andres.course.agy.springboot.springapi.app.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // We use @Lazy on PasswordEncoder to prevent a circular dependency with SecurityConfig
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.entityToDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserCreateDto userCreateDto) {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new EmailAlreadyExistsException(userCreateDto.email());
        }
        User user = userMapper.createDtoToEntity(userCreateDto);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.entityToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // If email is changing, check if the new email is already in use
        if (!user.getEmail().equalsIgnoreCase(dto.email()) &&
                userRepository.findByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        user.setEmail(dto.email());

        // Update password only if a new one is provided
        if (dto.password() != null && !dto.password().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        // Update roles if provided
        if (dto.roles() != null && !dto.roles().isBlank()) {
            user.setRoles(dto.roles());
        }

        User savedUser = userRepository.save(user);
        return userMapper.entityToDto(savedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
