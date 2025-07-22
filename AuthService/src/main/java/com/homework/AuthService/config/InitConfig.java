package com.homework.AuthService.config;

import com.homework.AuthService.model.Role;
import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.repository.RoleRepository;
import com.homework.AuthService.repository.UserEntityRepository;
import com.homework.AuthService.service.UserEntityService;
import com.homework.AuthService.settings.Constants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitConfig {

    @Bean
    public CommandLineRunner init(
            UserEntityService userEntityService,
            RoleRepository roleRepository,
            UserEntityRepository userEntityRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Role adminRole = roleRepository.findByTitle(Constants.ADMIN_ROLE_TITLE).orElseGet(() -> {
                Role role = new Role();
                role.setTitle(Constants.ADMIN_ROLE_TITLE);
                return roleRepository.save(role);
            });
            Role userRole = roleRepository.findByTitle(Constants.USER_ROLE_TITLE).orElseGet(() -> {
                Role role = new Role();
                role.setTitle(Constants.USER_ROLE_TITLE);
                return roleRepository.save(role);
            });

            Role premiumUserRole = roleRepository.findByTitle(Constants.PREMIUM_USER_ROLE_TITLE).orElseGet(() -> {
                Role role = new Role();
                role.setTitle(Constants.PREMIUM_USER_ROLE_TITLE);
                return roleRepository.save(role);
            });

            UserEntity adminUser = new UserEntity();
            adminUser.setUsername("testadmin");
            adminUser.setPassword(passwordEncoder.encode("mypassword"));
            adminUser.setEmail("testadmin@homework.com");
            adminUser.addRole(adminRole);
            adminUser.addRole(userRole);
            userEntityRepository.save(adminUser);

            UserEntity commonUser = new UserEntity();
            commonUser.setUsername("testuser");
            commonUser.setPassword(passwordEncoder.encode("mypassword"));
            commonUser.setEmail("testuser@homework.com");
            commonUser.addRole(userRole);
            userEntityRepository.save(commonUser);

            UserEntity premiumUser = new UserEntity();
            premiumUser.setUsername("testpremuser");
            premiumUser.setPassword(passwordEncoder.encode("mypassword"));
            premiumUser.setEmail("testpremuser@homework.com");
            premiumUser.addRole(premiumUserRole);
            userEntityRepository.save(premiumUser);
        };
    }
}
