package com.homework.AuthService.security;

import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEntityDetailsService implements UserDetailsService {
    @Autowired
    UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optUser = userEntityRepository.findByUsername(username);
        UserEntity userEntity = optUser.orElseThrow(() -> new UsernameNotFoundException(username));
        return new UserEntityDetails(userEntity);
    }
}
