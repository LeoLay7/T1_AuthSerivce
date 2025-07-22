import com.homework.AuthService.dto.UserRegistrationDto;
import com.homework.AuthService.error.InvalidPasswordException;
import com.homework.AuthService.error.RoleNotFoundException;
import com.homework.AuthService.mapper.UserRegistrationMapper;
import com.homework.AuthService.model.Role;
import com.homework.AuthService.model.UserEntity;
import com.homework.AuthService.repository.RoleRepository;
import com.homework.AuthService.repository.UserEntityRepository;
import com.homework.AuthService.service.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserEntityServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRegistrationMapper userRegistrationMapper;

    @InjectMocks
    private UserEntityService userEntityService;

    private UserRegistrationDto userDto;
    private UserEntity userEntity;
    private Role defaultRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Подготовка тестовых данных
        userDto = new UserRegistrationDto();
        userDto.setUsername("newuser");
        userDto.setPassword("Password123");
        userDto.setEmail("new@example.com");

        userEntity = new UserEntity();
        userEntity.setUsername("newuser");
        userEntity.setEmail("new@example.com");

        defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setTitle("ROLE_USER");
    }

    @Test
    void save_WithValidUser_ShouldSaveUser() {
        // Given
        when(roleRepository.findByTitle("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userRegistrationMapper.toEntity(userDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded_password");

        // When
        userEntityService.save(userDto);

        // Then
        verify(userEntityRepository).save(userEntity);
        assertEquals("encoded_password", userEntity.getPassword());
        assertTrue(userEntity.getRoles().contains(defaultRole));
    }

    @Test
    void save_WithMissingDefaultRole_ShouldThrowRoleNotFoundException() {
        // Given
        when(roleRepository.findByTitle("ROLE_USER")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class, () -> userEntityService.save(userDto));
        verify(userEntityRepository, never()).save(any());
    }
}
