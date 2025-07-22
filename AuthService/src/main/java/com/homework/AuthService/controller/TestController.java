package com.homework.AuthService.controller;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test with no roles");
    }
    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> testUser() {
        return ResponseEntity.ok("User test");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> testAdmin() {
        return ResponseEntity.ok("Admin test");
    }

    @GetMapping("/premium-user")
    @PreAuthorize("hasRole('ROLE_PREMIUM_USER')")
    public ResponseEntity<String> testPremiumUser() {
        return ResponseEntity.ok("Premium user test");
    }
}
