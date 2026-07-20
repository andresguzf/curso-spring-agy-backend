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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Clean origins (remove trailing slashes, trim spaces)
        List<String> cleanedOrigins = allowedOrigins.stream()
                .map(String::trim)
                .map(origin -> origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin)
                .toList();

        logger.info("Configuring CORS with allowed origins: {}", cleanedOrigins);

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(cleanedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
