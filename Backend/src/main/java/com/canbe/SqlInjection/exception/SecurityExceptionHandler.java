package com.canbe.SqlInjection.exception;

import com.canbe.SqlInjection.dto.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Response> handleSecurityException(SecurityException ex, HttpServletRequest request) {

        logger.warn("Security violation detected from IP: {} - URI: {} - Error: {}",
                getClientIP(request), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(
                Response.builder()
                        .status(400)
                        .message("Invalid input detected")
                        .build()
        );
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}