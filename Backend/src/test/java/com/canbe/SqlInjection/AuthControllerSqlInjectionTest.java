package com.canbe.SqlInjection;

import com.canbe.SqlInjection.dto.LoginRequest;
import com.canbe.SqlInjection.dto.RegistrationRequest;
import com.canbe.SqlInjection.model.User;
import com.canbe.SqlInjection.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerSqlInjectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // Insert a valid user (password should be encoded in real app — insert via register endpoint or encode manually)
        // For simplicity, register via controller or directly insert encoded password if PasswordEncoder available.
    }

    @Test
    @DisplayName("Login should not be bypassed by SQL injection payload in email")
    void loginShouldNotBeBypassedBySqlInjection() throws Exception {
        // First register a normal user through the register endpoint
        RegistrationRequest reg = new RegistrationRequest();
        reg.setFirstName("John");
        reg.setLastName("Doe");
        reg.setEmail("john@example.com");
        reg.setPhoneNumber("1234567890");
        reg.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        // Now attempt to login using an SQL injection-like email payload
        LoginRequest loginPayload = new LoginRequest();
        loginPayload.setEmail("anything' OR '1'='1");
        loginPayload.setPassword("whatever");

        // Expectation: Should NOT be successful (4xx). Could be 404/401 depending on exception handlers.
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Login should fail when password contains SQL metacharacters")
    void loginWithSqlCharsInPassword_shouldNotBypass() throws Exception {
        // register a known user
        RegistrationRequest reg = new RegistrationRequest();
        reg.setFirstName("Alice");
        reg.setLastName("Smith");
        reg.setEmail("alice@example.com");
        reg.setPhoneNumber("0987654321");
        reg.setPassword("SecurePass!1");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        // Attempt login with correct email but password containing SQL payload (should be invalid)
        LoginRequest login = new LoginRequest();
        login.setEmail("alice@example.com");
        login.setPassword("' OR '1'='1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().is4xxClientError());
    }
}
