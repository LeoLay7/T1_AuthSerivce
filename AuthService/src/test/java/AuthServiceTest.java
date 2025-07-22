import com.homework.AuthService.error.AuthException;
import com.homework.AuthService.model.Role;
import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.request.AuthRequest;
import com.homework.AuthService.response.JwtResponse;
import com.homework.AuthService.security.UserEntityDetails;
import com.homework.AuthService.security.UserEntityDetailsService;
import com.homework.AuthService.security.UserTokenService;
import com.homework.AuthService.security.jwt.JwtProvider;
import com.homework.AuthService.service.AuthService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserEntityDetailsService userEntityDetailsService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private Claims claims;

    @InjectMocks
    private AuthService authService;

    private UserEntityDetails userDetails;
    private AuthRequest authRequest;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Подготовка тестовых данных
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded_password");
        user.setEmail("test@example.com");
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        role.setTitle("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);

        userDetails = new UserEntityDetails(user);

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        refreshToken = "valid_refresh_token";
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() {
        // Given
        when(userEntityDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtProvider.generateAccessToken(userDetails)).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken(userDetails)).thenReturn("refresh_token");

        // When
        JwtResponse response = authService.login(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        verify(userTokenService).addOrUpdateToken(1L, "refresh_token");
    }

    @Test
    void login_WithInvalidUsername_ShouldThrowAuthException() {
        // Given
        when(userEntityDetailsService.loadUserByUsername("testuser"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // When & Then
        assertThrows(AuthException.class, () -> authService.login(authRequest));
    }

    @Test
    void login_WithDisabledUser_ShouldThrowDisabledException() {
        // Given
        UserEntity disabledUser = new UserEntity();
        disabledUser.setId(2L);
        disabledUser.setUsername("disabled");
        disabledUser.setPassword("encoded_password");
        disabledUser.setEnabled(false);

        UserEntityDetails disabledDetails = new UserEntityDetails(disabledUser);

        when(userEntityDetailsService.loadUserByUsername("testuser")).thenReturn(disabledDetails);

        // When & Then
        assertThrows(DisabledException.class, () -> authService.login(authRequest));
        verify(userTokenService).revokeUserToken(2L);
    }

    @Test
    void refreshTokens_WithValidToken_ShouldReturnNewTokens() {
        // Given
        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(claims.get("id", Long.class)).thenReturn(1L);
        when(claims.getSubject()).thenReturn("testuser");
        when(userTokenService.isTokenValid(1L, refreshToken)).thenReturn(true);
        when(userEntityDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtProvider.generateAccessToken(userDetails)).thenReturn("new_access_token");
        when(jwtProvider.generateRefreshToken(userDetails)).thenReturn("new_refresh_token");

        // When
        JwtResponse response = authService.refreshTokens(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
        verify(userTokenService).addOrUpdateToken(1L, "new_refresh_token");
    }

    @Test
    void refreshTokens_WithInvalidToken_ShouldThrowAuthException() {
        // Given
        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(AuthException.class, () -> authService.refreshTokens(refreshToken));
    }

    @Test
    void logout_WithValidToken_ShouldRevokeToken() {
        // Given
        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(claims.get("id", Long.class)).thenReturn(1L);
        when(userTokenService.isTokenValid(1L, refreshToken)).thenReturn(true);

        // When
        authService.logout(refreshToken);

        // Then
        verify(userTokenService).revokeUserToken(1L);
    }
}
