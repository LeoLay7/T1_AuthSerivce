package com.homework.AuthService.service;

import com.homework.AuthService.error.AuthException;
import com.homework.AuthService.request.AuthRequest;
import com.homework.AuthService.response.JwtResponse;
import com.homework.AuthService.security.UserEntityDetails;
import com.homework.AuthService.security.UserEntityDetailsService;
import com.homework.AuthService.security.UserTokenService;
import com.homework.AuthService.security.jwt.JwtProvider;
import com.homework.AuthService.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserEntityDetailsService userEntityDetailsService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService userTokenService;

    public JwtResponse login(@NonNull AuthRequest request) {
        final UserEntityDetails userDetails;
        try {
            userDetails = (UserEntityDetails) userEntityDetailsService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new AuthException("Wrong username or password");
        }

        if (!userDetails.isEnabled()) {
            userTokenService.revokeUserToken(userDetails.getId());
            throw new DisabledException("User is disabled");
        }

        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(userDetails);
            final String refreshToken = jwtProvider.generateRefreshToken(userDetails);
            userTokenService.addOrUpdateToken(userDetails.getId(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Wrong username or password");
        }
    }

    public JwtResponse refreshTokens(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final long userId = claims.get("id", Long.class);
            final String username = claims.getSubject();
            if (userTokenService.isTokenValid(userId, refreshToken)) {
                UserEntityDetails userDetails = (UserEntityDetails) userEntityDetailsService.loadUserByUsername(username);
                final String newAccessToken = jwtProvider.generateAccessToken(userDetails);
                final String newRefreshToken = jwtProvider.generateRefreshToken(userDetails);
                userTokenService.addOrUpdateToken(userId, newRefreshToken);
                return new JwtResponse(newAccessToken, newRefreshToken);
            }
            throw new AuthException("Wrong refresh token ot token was revoked");
        }
        throw new AuthException("Invalid refresh token");
    }

    public void logout(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final long userId = claims.get("id", Long.class);
            if (userTokenService.isTokenValid(userId, refreshToken)) {
                userTokenService.revokeUserToken(userId);
                return;
            }
            throw new AuthException("Wrong refresh token ot token was revoked");
        }
        throw new AuthException("Invalid refresh token");
    }
}
