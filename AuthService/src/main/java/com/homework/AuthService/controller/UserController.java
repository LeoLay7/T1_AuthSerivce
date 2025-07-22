package com.homework.AuthService.controller;

import com.homework.AuthService.dto.UserRegistrationDto;
import com.homework.AuthService.service.UserEntityService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private UserEntityService userEntityService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        userEntityService.save(userRegistrationDto);
        return ResponseEntity.ok().build();
    }
}
