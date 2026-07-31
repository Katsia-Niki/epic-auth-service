package by.nikiforova.epic_auth_service.service;

import by.nikiforova.epic_auth_service.dto.request.CredentialRequestDto;
import by.nikiforova.epic_auth_service.dto.response.CredentialResponseDto;
import by.nikiforova.epic_auth_service.entity.Credential;
import by.nikiforova.epic_auth_service.entity.Role;
import by.nikiforova.epic_auth_service.exception.CredentialAlreadyExistsException;
import by.nikiforova.epic_auth_service.repository.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CredentialService credentialService;

    private CredentialRequestDto credentialRequestDto;

    private Credential savedCredential;

    @BeforeEach
    void setUp() {
        credentialRequestDto = new CredentialRequestDto(1L, "liza", "password123", Role.USER);

        savedCredential = Credential.builder()
                .userId(1L)
                .login("liza")
                .passwordHash("encoded-password")
                .role(Role.USER)
                .build();
        savedCredential.setId(10L);
        savedCredential.setCreatedAt(LocalDateTime.now());
        savedCredential.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("save credentials - success")
    void saveWhenLoginUniqueShouldSaveCredential() {
        when(credentialRepository.existsByLogin("liza")).thenReturn(false);
        when(credentialRepository.existsByUserId(1L)).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(credentialRepository.save(any(Credential.class))).thenReturn(savedCredential);

        CredentialResponseDto result = credentialService.save(credentialRequestDto);

        assertEquals(10L, result.id());
        assertEquals(1L, result.userId());
        assertEquals("liza", result.login());
        assertEquals(Role.USER, result.role());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());

        verify(passwordEncoder).encode("password123");
        verify(credentialRepository).save(any(Credential.class));
    }

    @Test
    @DisplayName("save credentials - CredentialAlreadyExistsException when login exists")
    void saveWhenLoginExistsShouldThrowCredentialAlreadyExistsException() {
        when(credentialRepository.existsByLogin("liza")).thenReturn(true);

        CredentialAlreadyExistsException exception = assertThrows(
                CredentialAlreadyExistsException.class,
                () -> credentialService.save(credentialRequestDto)
        );

        assertEquals("Login already exists: liza", exception.getMessage());

        verify(credentialRepository, never()).save(any());
    }

    @Test
    @DisplayName("save credentials - CredentialAlreadyExistsException when user id exists")
    void saveWhenUserIdExistsShouldThrowCredentialAlreadyExistsException() {
        when(credentialRepository.existsByUserId(1L)).thenReturn(true);

        CredentialAlreadyExistsException exception = assertThrows(
                CredentialAlreadyExistsException.class,
                () -> credentialService.save(credentialRequestDto)
        );

        assertEquals("UserId already exists: 1", exception.getMessage());

        verify(credentialRepository, never()).save(any());
    }
}
