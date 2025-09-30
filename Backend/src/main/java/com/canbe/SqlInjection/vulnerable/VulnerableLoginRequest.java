package com.canbe.SqlInjection.vulnerable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VulnerableLoginRequest {

    private String email;
    private String password;
}