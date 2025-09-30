package com.canbe.SqlInjection.vulnerable;

import com.canbe.SqlInjection.dto.Response;
import com.canbe.SqlInjection.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VulnerableUserService {

    private final EntityManager entityManager;

    public Response vulnerableLogin(VulnerableLoginRequest loginRequest) {

        String sql = "SELECT * FROM users WHERE email = '" +
                loginRequest.getEmail() +
                "' AND password = '" +
                loginRequest.getPassword() + "'";

        log.warn("VULNERABLE SQL: {}", sql);

        Query query = entityManager.createNativeQuery(sql, User.class);

        try {
            @SuppressWarnings("unchecked")
            List<User> users = query.getResultList();

            if (!users.isEmpty()) {
                User user = users.get(0);
                return Response.builder()
                        .status(200)
                        .message("Login successful")
                        .user(null)
                        .build();
            } else {
                return Response.builder()
                        .status(401)
                        .message("Invalid credentials")
                        .build();
            }
        } catch (Exception e) {
            log.error("SQL Error: {}", e.getMessage());
            return Response.builder()
                    .status(500)
                    .message("Database error: " + e.getMessage())
                    .build();
        }
    }
}