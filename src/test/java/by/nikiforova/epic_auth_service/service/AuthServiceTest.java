package by.nikiforova.epic_auth_service.service;

import by.nikiforova.epic_auth_service.client.UserServiceClient;
import by.nikiforova.epic_auth_service.dto.request.LoginRequestDto;
import by.nikiforova.epic_auth_service.dto.request.TokenRequestDto;
import by.nikiforova.epic_auth_service.dto.request.UserCreateRequestDto;
import by.nikiforova.epic_auth_service.dto.request.UserRegistrationRequestDto;
import by.nikiforova.epic_auth_service.dto.response.JwtResponseDto;
import by.nikiforova.epic_auth_service.dto.response.TokenValidateResponseDto;
import by.nikiforova.epic_auth_service.dto.response.UserResponseDto;
import by.nikiforova.epic_auth_service.entity.Credential;
import by.nikiforova.epic_auth_service.entity.Role;
import by.nikiforova.epic_auth_service.exception.CredentialAlreadyExistsException;
import by.nikiforova.epic_auth_service.exception.InvalidCredentialsException;
import by.nikiforova.epic_auth_service.exception.InvalidTokenException;
import by.nikiforova.epic_auth_service.repository.CredentialRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String VALID_TOKEN = "valid-token";
    private static final String PASSWORD_HASH = "password-hash";

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequestDto;

    private Credential credential;

    private UserRegistrationRequestDto registrationRequestDto;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto("liza", "password123");
        credential = Credential.builder()
                .userId(1L)
                .login("liza")
                .passwordHash(PASSWORD_HASH)
                .role(Role.USER)
                .build();
        registrationRequestDto = new UserRegistrationRequestDto(
                "Liza",
                "Ivanova",
                "liza@gmail.com",
                LocalDate.of(1995, Month.MAY, 10),
                "liza",
                "password123"
        );
    }

    @Test
    @DisplayName("register - success")
    void registerWhenLoginIsUniqueShouldReturnTokens() {
        UserResponseDto createdUser = new UserResponseDto(
                1L,
                "Liza",
                "Ivanova",
                "liza@gmail.com",
                LocalDate.of(1995, Month.MAY, 10),
                true,
                null,
                null
        );

        when(credentialRepository.existsByLogin("liza")).thenReturn(false);
        when(userServiceClient.createUser(any(UserCreateRequestDto.class))).thenReturn(createdUser);
        when(passwordEncoder.encode("password123")).thenReturn(PASSWORD_HASH);
        when(jwtService.generateAccessToken(1L, Role.USER, "liza")).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(1L, Role.USER, "liza")).thenReturn(REFRESH_TOKEN);

        JwtResponseDto result = authService.register(registrationRequestDto);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());

        verify(userServiceClient).createUser(any(UserCreateRequestDto.class));
        verify(credentialRepository).save(any(Credential.class));
    }

    @Test
    @DisplayName("register - CredentialAlreadyExistsException")
    void registerWhenLoginExistsShouldThrowException() {
        when(credentialRepository.existsByLogin("liza")).thenReturn(true);

        assertThrows(CredentialAlreadyExistsException.class,
                () -> authService.register(registrationRequestDto));

        verify(userServiceClient, never()).createUser(any());
        verify(credentialRepository, never()).save(any());
    }

    @Test
    @DisplayName("register - rollback user when save credentials fails")
    void registerWhenSaveFailsShouldDeleteUserAndRethrow() {
        UserResponseDto createdUser = new UserResponseDto(
                1L,
                "Liza",
                "Ivanova",
                "liza@gmail.com",
                LocalDate.of(1995, Month.MAY, 10),
                true,
                null,
                null
        );

        when(credentialRepository.existsByLogin("liza")).thenReturn(false);
        when(userServiceClient.createUser(any(UserCreateRequestDto.class))).thenReturn(createdUser);
        when(passwordEncoder.encode("password123")).thenReturn(PASSWORD_HASH);
        when(credentialRepository.save(any(Credential.class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(registrationRequestDto)
        );

        assertEquals("DB error", exception.getMessage());

        verify(userServiceClient).deleteUser(1L);
        verify(jwtService, never()).generateAccessToken(any(), any(), any());
    }

    @Test
    @DisplayName("authenticate - success")
    void authenticateWhenValidCredentialsShouldReturnTokens() {
        when(credentialRepository.findByLogin("liza")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("password123", PASSWORD_HASH)).thenReturn(true);
        when(jwtService.generateAccessToken(1L, Role.USER, "liza")).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(1L, Role.USER, "liza")).thenReturn(REFRESH_TOKEN);

        JwtResponseDto result = authService.authenticate(loginRequestDto);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());

        verify(passwordEncoder).matches("password123", PASSWORD_HASH);
        verify(jwtService).generateAccessToken(1L, Role.USER, "liza");
        verify(jwtService).generateRefreshToken(1L, Role.USER, "liza");
    }

    @Test
    @DisplayName("authenticate - InvalidCredentialsException when login not found")
    void authenticateWhenLoginNotFoundShouldThrowInvalidCredentialsException() {
        when(credentialRepository.findByLogin("liza")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate(loginRequestDto)
        );

        assertEquals("Invalid login or password", exception.getMessage());
        verify(jwtService, never()).generateAccessToken(1L, Role.USER, "liza");
    }

    @Test
    @DisplayName("authenticate - InvalidCredentialsException when wrong password")
    void authenticateWhenWrongPasswordShouldThrowInvalidCredentialsException() {
        when(credentialRepository.findByLogin("liza")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("wrong-password", PASSWORD_HASH)).thenReturn(false);
        LoginRequestDto wrongPasswordRequest = new LoginRequestDto("liza", "wrong-password");

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate(wrongPasswordRequest)
        );

        assertEquals("Invalid login or password", exception.getMessage());
        verify(jwtService, never()).generateAccessToken(1L, Role.USER, "liza");
    }

    @Test
    @DisplayName("validate - success")
    void validateWhenTokenValidShouldReturnClaims() {
        Claims claims = mock(Claims.class);

        when(claims.get("userId", Long.class)).thenReturn(1L);
        when(claims.get("role", String.class)).thenReturn("USER");
        when(claims.getSubject()).thenReturn("liza");
        when(jwtService.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtService.parseToken(VALID_TOKEN)).thenReturn(claims);

        TokenValidateResponseDto result = authService.validate(new TokenRequestDto(VALID_TOKEN));

        assertTrue(result.valid());
        assertEquals(1L, result.userId());
        assertEquals(Role.USER, result.role());
        assertEquals("liza", result.login());
    }

    @Test
    @DisplayName("validate - InvalidTokenException")
    void validateWhenTokenInvalidShouldThrowInvalidTokenException() {
        when(jwtService.validateToken("not-a-token")).thenReturn(false);
        TokenRequestDto request = new TokenRequestDto("not-a-token");

        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> authService.validate(request)
        );

        assertEquals("Invalid or expired token", exception.getMessage());

        verify(jwtService, never()).parseToken("not-a-token");
    }

    @Test
    @DisplayName("refresh - success")
    void refreshTokenWhenTokenValidShouldReturnNewTokens() {
        Claims claims = mock(Claims.class);
        
        when(claims.get("userId", Long.class)).thenReturn(1L);
        when(claims.get("role", String.class)).thenReturn("USER");
        when(claims.getSubject()).thenReturn("liza");
        when(jwtService.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtService.parseToken(VALID_TOKEN)).thenReturn(claims);
        when(jwtService.generateAccessToken(1L, Role.USER, "liza")).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(1L, Role.USER, "liza")).thenReturn("new-refresh-token");

        JwtResponseDto result = authService.refreshToken(new TokenRequestDto(VALID_TOKEN));

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
    }

    @Test
    @DisplayName("refresh - InvalidTokenException")
    void refreshTokenWhenTokenInvalidShouldThrowInvalidTokenException() {
        when(jwtService.validateToken("not-a-token")).thenReturn(false);
        TokenRequestDto request = new TokenRequestDto("not-a-token");

        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> authService.refreshToken(request)
        );

        assertEquals("Invalid or expired token", exception.getMessage());

        verify(jwtService, never()).parseToken("not-a-token");
    }
}
