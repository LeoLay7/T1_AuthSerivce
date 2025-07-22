package com.homework.AuthService.mapper;

import com.homework.AuthService.dto.UserRegistrationDto;
import com.homework.AuthService.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {
    public UserRegistrationMapper() {}

    public UserEntity toEntity(UserRegistrationDto dto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(dto.getEmail());
        userEntity.setPassword(dto.getPassword());
        userEntity.setUsername(dto.getUsername());
        return userEntity;
    }
}
