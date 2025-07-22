package com.homework.AuthService.security.jwt;

import com.homework.AuthService.error.InvalidTokenException;
import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.security.UserEntityDetails;
import com.homework.AuthService.settings.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull UserEntityDetails userDetails) {
        return generateToken(userDetails, Constants.accessTokenSeconds, jwtAccessSecret);
    }

    public String generateRefreshToken(@NonNull UserEntityDetails userDetails) {
        return generateToken(userDetails, Constants.refreshTokenSeconds, jwtRefreshSecret);
    }

    private String generateToken(@NonNull UserEntityDetails userDetails, long expirationSeconds, Key secret) {
        final Instant now = Instant.now();
        final Instant expirationInstant = now.plusSeconds(expirationSeconds);
        final Date expiration = Date.from(expirationInstant);

        List<Map<String, Object>> roles = userDetails.getRoles().stream()
                .map(role -> {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("id", role.getId());
                    roleMap.put("title", role.getTitle());
                    return roleMap;
                })
                .toList();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setExpiration(expiration)
                .signWith(secret)
                .claim("roles", roles)
                .claim("id", userDetails.getId())
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token,@NonNull Key secret) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException expEx) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
