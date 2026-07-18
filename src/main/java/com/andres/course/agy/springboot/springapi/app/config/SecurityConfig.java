package com.andres.course.agy.springboot.springapi.app.config;

import com.andres.course.agy.springboot.springapi.app.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Authentication endpoints
                .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                
                // Registering is public
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                
                // Managing users is restricted to ADMIN
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // Read operations on customers are allowed for both USER and ADMIN roles
                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyRole("USER", "ADMIN")
                
                // Write/Modify operations on customers are restricted to ADMIN
                .requestMatchers(HttpMethod.POST, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

                // Read operations on invoices are allowed for both USER and ADMIN roles
                .requestMatchers(HttpMethod.GET, "/api/invoices/**").hasAnyRole("USER", "ADMIN")
                
                // Write/Modify operations on invoices are restricted to ADMIN
                .requestMatchers(HttpMethod.POST, "/api/invoices/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/invoices/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/invoices/**").hasRole("ADMIN")
                
                // Allow H2 console
                .requestMatchers("/h2-console/**").permitAll()
                
                // Profile information
                .requestMatchers("/api/auth/me").authenticated()
                
                .anyRequest().authenticated()
            )
            // Allow frames for H2 console
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
