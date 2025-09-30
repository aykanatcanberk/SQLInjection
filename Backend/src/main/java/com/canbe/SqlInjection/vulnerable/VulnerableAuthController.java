package com.canbe.SqlInjection.vulnerable;

import com.canbe.SqlInjection.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vulnerable")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VulnerableAuthController {

    private final VulnerableUserService vulnerableUserService;

    @PostMapping("/login")
    public ResponseEntity<Response> vulnerableLogin(@RequestBody VulnerableLoginRequest request) {
        return ResponseEntity.ok(vulnerableUserService.vulnerableLogin(request));
    }
}