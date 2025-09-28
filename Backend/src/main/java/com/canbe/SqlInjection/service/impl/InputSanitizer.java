package com.canbe.SqlInjection.service.impl;

import com.canbe.SqlInjection.dto.LoginRequest;
import com.canbe.SqlInjection.dto.RegistrationRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InputSanitizer {

    private static final List<String> SQL_INJECTION_PATTERNS = Arrays.asList(
            "('|(\\-\\-)|;|/\\*|\\*/|#)",
            "\\b(union|select|insert|update|delete|drop|alter|create|exec|execute)\\b",
            "(\\bor\\b|\\band\\b).*(=|like)",
            "(%00|\\\\x00|\\x00)",
            "[\\x00-\\x1F\\x7F]"
    );

    private static final List<String> XSS_PATTERNS = Arrays.asList(
            "<script[^>]*>.*?</script>",
            "<iframe[^>]*>.*?</iframe>",
            "javascript:",
            "vbscript:",
            "onload=",
            "onerror=",
            "onclick="
    );

    public String sanitizeInput(String input) {
        if (input == null) return null;

        for (String pattern : SQL_INJECTION_PATTERNS) {
            if (input.matches("(?i).*" + pattern + ".*")) {
                throw new SecurityException("Potentially malicious input detected");
            }
        }

        for (String pattern : XSS_PATTERNS) {
            if (input.matches("(?i).*" + pattern + ".*")) {
                throw new SecurityException("Potentially malicious input detected");
            }
        }

        // HTML encoding
        return org.springframework.web.util.HtmlUtils.htmlEscape(input).trim();
    }

    public void validateAndSanitizeLoginRequest(LoginRequest request) {
        request.setEmail(sanitizeInput(request.getEmail()));
        request.setPassword(sanitizeInput(request.getPassword()));
    }

    public void validateAndSanitizeRegistrationRequest(RegistrationRequest request) {
        request.setFirstName(sanitizeInput(request.getFirstName()));
        request.setLastName(sanitizeInput(request.getLastName()));
        request.setEmail(sanitizeInput(request.getEmail()));
        request.setPhoneNumber(sanitizeInput(request.getPhoneNumber()));
        request.setPassword(sanitizeInput(request.getPassword()));
    }
}