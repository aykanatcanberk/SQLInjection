package com.canbe.SqlInjection.dto;

import com.canbe.SqlInjection.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //generic
    private int status;
    private String message;

    //login
    private String token;
    private UserRole role;
    private Boolean isActive;
    private String expirationTime;

    //user data
    private UserDto user;
    private List<UserDto> users;

    private final LocalDateTime timestamp = LocalDateTime.now();

}