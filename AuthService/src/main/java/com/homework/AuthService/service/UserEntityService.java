package com.homework.AuthService.service;

import com.homework.AuthService.dto.UserRegistrationDto;
import com.homework.AuthService.error.InvalidPasswordException;
import com.homework.AuthService.error.RoleNotFoundException;
import com.homework.AuthService.mapper.UserRegistrationMapper;
import com.homework.AuthService.model.Role;
import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.repository.RoleRepository;
import com.homework.AuthService.repository.UserEntityRepository;
import com.homework.AuthService.settings.Constants;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserEntityService {
    private UserEntityRepository userEntityRepository;
    private PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRegistrationMapper userRegistrationMapper;

    public void save(UserRegistrationDto newUser) {
        String defaultRoleTitle = Constants.USER_ROLE_TITLE;
        Role defaultRole = roleRepository.findByTitle(defaultRoleTitle).orElseThrow(
                () -> new RoleNotFoundException("Default role not found. Default role: " + defaultRoleTitle)
        );

        String password = newUser.getPassword();
        validatePassword(password);

        UserEntity userEntity = userRegistrationMapper.toEntity(newUser);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.addRole(defaultRole);
        userEntityRepository.save(userEntity);
    }

    private void validatePassword(String password) throws InvalidPasswordException {
        if (password == null || password.length() < 8) {
            throw new InvalidPasswordException("Password should be at least 8 characters long");
        }
        boolean isContainsChars = false;
        boolean isContainsDigits = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                isContainsDigits = true;
            } else if (Character.isLetter(c)) {
                isContainsChars = true;
            }
        }
        if (!isContainsChars || !isContainsDigits) {
            throw new InvalidPasswordException("Password should contain digits and letters");
        }
    }
}
