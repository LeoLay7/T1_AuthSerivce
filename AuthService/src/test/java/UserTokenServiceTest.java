import com.homework.AuthService.model.UserToken;
import com.homework.AuthService.repository.UserTokenRepository;
import com.homework.AuthService.security.UserTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserTokenServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private UserTokenService userTokenService;

    private UserToken userToken;
    private final long userId = 1L;
    private final String token = "test_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userToken = new UserToken(userId, token);
    }

    @Test
    void addOrUpdateToken_WithNewUser_ShouldSaveNewToken() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        userTokenService.addOrUpdateToken(userId, token);

        // Then
        verify(userTokenRepository).save(any(UserToken.class));
    }

    @Test
    void addOrUpdateToken_WithExistingUser_ShouldUpdateToken() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.of(userToken));

        // When
        String newToken = "new_token";
        userTokenService.addOrUpdateToken(userId, newToken);

        // Then
        assertEquals(newToken, userToken.getToken());
        assertFalse(userToken.isRevoked());
        verify(userTokenRepository).save(userToken);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.of(userToken));

        // When
        boolean result = userTokenService.isTokenValid(userId, token);

        // Then
        assertTrue(result);
    }

    @Test
    void isTokenValid_WithRevokedToken_ShouldReturnFalse() {
        // Given
        userToken.setRevoked(true);
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.of(userToken));

        // When
        boolean result = userTokenService.isTokenValid(userId, token);

        // Then
        assertFalse(result);
    }

    @Test
    void isTokenValid_WithDifferentToken_ShouldReturnFalse() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.of(userToken));

        // When
        boolean result = userTokenService.isTokenValid(userId, "different_token");

        // Then
        assertFalse(result);
    }

    @Test
    void isTokenValid_WithNonExistentUser_ShouldReturnFalse() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        boolean result = userTokenService.isTokenValid(userId, token);

        // Then
        assertFalse(result);
    }

    @Test
    void revokeUserToken_WithExistingToken_ShouldRevokeToken() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.of(userToken));

        // When
        userTokenService.revokeUserToken(userId);

        // Then
        assertTrue(userToken.isRevoked());
        verify(userTokenRepository).save(userToken);
    }

    @Test
    void revokeUserToken_WithNonExistentToken_ShouldDoNothing() {
        // Given
        when(userTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        userTokenService.revokeUserToken(userId);

        // Then
        verify(userTokenRepository, never()).save(any());
    }
}
