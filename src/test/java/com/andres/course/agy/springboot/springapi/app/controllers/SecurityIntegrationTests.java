package com.andres.course.agy.springboot.springapi.app.controllers;

import com.andres.course.agy.springboot.springapi.app.dto.CustomerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SecurityIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private jakarta.servlet.Filter springSecurityFilterChain;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Manually build MockMvc using WebApplicationContext, applying Spring Security filter chain
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void testUnauthenticatedAccessBlocked() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginSuccessAndSetCookie() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("jwt_token");
        assertNotNull(cookie);
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }

    @Test
    void testLoginFailure() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"wrong_password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserRoleCanReadButNotModifyCustomers() throws Exception {
        // 1. Log in as user
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"user123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie jwtCookie = loginResult.getResponse().getCookie("jwt_token");
        assertNotNull(jwtCookie);

        // 2. Perform GET /api/customers -> should succeed
        mockMvc.perform(get("/api/customers").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10)); // preloaded customers count

        // 3. Perform POST /api/customers -> should be forbidden (403)
        CustomerDto newCustomer = new CustomerDto(null, "Charlie", "Brown", "charlie@example.com", "123", "Main St");
        String payload = objectMapper.writeValueAsString(newCustomer);

        mockMvc.perform(post("/api/customers")
                .cookie(jwtCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminRoleCanReadAndModifyCustomers() throws Exception {
        // 1. Log in as admin
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie jwtCookie = loginResult.getResponse().getCookie("jwt_token");
        assertNotNull(jwtCookie);

        // 2. Perform POST /api/customers -> should succeed (201 Created)
        CustomerDto newCustomer = new CustomerDto(null, "AdminCreated", "Customer", "admin.created@example.com", "999", "Admin St");
        String payload = objectMapper.writeValueAsString(newCustomer);

        MvcResult createResult = mockMvc.perform(post("/api/customers")
                .cookie(jwtCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("AdminCreated"))
                .andReturn();

        CustomerDto createdCustomer = objectMapper.readValue(createResult.getResponse().getContentAsString(), CustomerDto.class);

        // 3. Perform DELETE /api/customers/{id} -> should succeed (204 No Content)
        mockMvc.perform(delete("/api/customers/" + createdCustomer.id())
                .cookie(jwtCookie))
                .andExpect(status().isNoContent());
    }
}
