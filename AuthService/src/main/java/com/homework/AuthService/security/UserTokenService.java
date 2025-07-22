package com.homework.AuthService.security;

import com.homework.AuthService.model.UserToken;
import com.homework.AuthService.repository.UserTokenRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserTokenService {
    private UserTokenRepository userTokenRepository;

    public void addOrUpdateToken(long user_id, String token) {
        UserToken userToken = userTokenRepository.findByUserId(user_id).orElse(null);
        if (userToken == null) {
            userTokenRepository.save(new UserToken(user_id, token));
        } else {
            userToken.setToken(token);
            userToken.setRevoked(false);
            userTokenRepository.save(userToken);
        }
    }

    public boolean isTokenValid(long userId, String token) {
        UserToken userToken = userTokenRepository.findByUserId(userId).orElse(null);
        if (userToken == null) {
            return false;
        }
        return !userToken.isRevoked() && userToken.getToken().equals(token);
    }

    public boolean isUserTokenRevoked(long user_id) {
        UserToken userToken = userTokenRepository.findByUserId(user_id).orElse(null);
        if (userToken == null) {
            return false;
        }
        return userToken.isRevoked();
    }

    public void revokeUserToken(long user_id) {
        UserToken userToken = userTokenRepository.findByUserId(user_id).orElse(null);
        if (userToken == null) {
            return;
        }
        userToken.setRevoked(true);
        userTokenRepository.save(userToken);
    }
}
