import com.homework.AuthService.model.Role;
import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.security.UserEntityDetails;
import com.homework.AuthService.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private UserEntityDetails userDetails;

    @BeforeEach
    void setUp() {
        // Создаем JwtProvider с тестовыми секретами
        jwtProvider = new JwtProvider(
                "c2VjcmV0X2FjY2Vzc190b2tlbl9rZXlfZm9yX3Rlc3Rpbmc=", // Base64 encoded test key
                "c2VjcmV0X3JlZnJlc2hfdG9rZW5fa2V5X2Zvcl90ZXN0aW5n" // Base64 encoded test key
        );

        // Подготовка тестовых данных
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        role.setTitle("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);

        userDetails = new UserEntityDetails(user);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        // When
        String token = jwtProvider.generateAccessToken(userDetails);

        // Then
        assertNotNull(token);
        assertTrue(jwtProvider.validateAccessToken(token));

        Claims claims = jwtProvider.getAccessClaims(token);
        assertEquals("testuser", claims.getSubject());
        assertEquals(1L, claims.get("id", Long.class));

        List<Map<String, Object>> roles = claims.get("roles", List.class);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_USER", roles.get(0).get("title"));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        // When
        String token = jwtProvider.generateRefreshToken(userDetails);

        // Then
        assertNotNull(token);
        assertTrue(jwtProvider.validateRefreshToken(token));

        Claims claims = jwtProvider.getRefreshClaims(token);
        assertEquals("testuser", claims.getSubject());
        assertEquals(1L, claims.get("id", Long.class));
    }

    @Test
    void validateAccessToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        assertFalse(jwtProvider.validateAccessToken(invalidToken));
    }

    @Test
    void validateRefreshToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        assertFalse(jwtProvider.validateRefreshToken(invalidToken));
    }
}
