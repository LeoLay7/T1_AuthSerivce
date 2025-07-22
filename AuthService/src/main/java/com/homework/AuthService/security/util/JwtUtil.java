package com.homework.AuthService.security.util;

import com.homework.AuthService.model.Role;
import com.homework.AuthService.security.jwt.JwtAuthentication;
import io.jsonwebtoken.Claims;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public class JwtUtil {
    public static String cutAuthorizationToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7);
    }
    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setId(claims.get("id", Long.class));
        jwtAuthentication.setEmail(claims.getSubject());
        jwtAuthentication.setRoles(getRoles(claims));
        return jwtAuthentication;
    }

    private static List<Role> getRoles(@NonNull Claims claims) {
        final List<Map<String, Object>> rolesData = claims.get("roles", List.class);
        return rolesData.stream()
                .map(roleMap -> {
                    Role role = new Role();
                    role.setId(((Number) roleMap.get("id")).intValue());
                    role.setTitle((String) roleMap.get("title"));
                    return role;
                })
                .toList();
    }
}