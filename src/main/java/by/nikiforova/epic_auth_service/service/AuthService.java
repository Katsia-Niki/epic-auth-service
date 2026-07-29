package by.nikiforova.epic_auth_service.service;

import by.nikiforova.epic_auth_service.dto.request.LoginRequestDto;
import by.nikiforova.epic_auth_service.dto.request.TokenRequestDto;
import by.nikiforova.epic_auth_service.dto.response.JwtResponseDto;
import by.nikiforova.epic_auth_service.dto.response.TokenValidateResponseDto;
import by.nikiforova.epic_auth_service.entity.Credential;
import by.nikiforova.epic_auth_service.entity.Role;
import by.nikiforova.epic_auth_service.exception.InvalidCredentialsException;
import by.nikiforova.epic_auth_service.exception.InvalidTokenException;
import by.nikiforova.epic_auth_service.repository.CredentialRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtResponseDto authenticate(LoginRequestDto loginRequestDto){
        Credential credential = credentialRepository.findByLogin(loginRequestDto.login())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password"));

        if (!passwordEncoder.matches(loginRequestDto.password(), credential.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid login or password");
        }

        String accessToken = jwtService.generateAccessToken(credential.getUserId(),
                credential.getRole(), credential.getLogin());

        String refreshToken = jwtService.generateRefreshToken(credential.getUserId(),
                credential.getRole(), credential.getLogin());

        return new JwtResponseDto(accessToken, refreshToken);
    }

    public TokenValidateResponseDto validate (TokenRequestDto tokenRequestDto) {

        if(!jwtService.validateToken(tokenRequestDto.token())) {
            throw new InvalidTokenException("Invalid or expired token");
        }
        Claims claims = jwtService.parseToken(tokenRequestDto.token());

        return new TokenValidateResponseDto(true, claims.get("userId", Long.class),
                Role.valueOf(claims.get("role", String.class)),
                claims.getSubject());
    }

    public JwtResponseDto refreshToken(TokenRequestDto tokenRequestDto) {

        if(!jwtService.validateToken(tokenRequestDto.token())) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        Claims claims = jwtService.parseToken(tokenRequestDto.token());
        Long userId = claims.get("userId", Long.class);
        Role role = Role.valueOf(claims.get("role", String.class));
        String login = claims.getSubject();

        String accessToken = jwtService.generateAccessToken(userId, role, login);
        String refreshToken = jwtService.generateRefreshToken(userId, role, login);

        return new JwtResponseDto(accessToken, refreshToken);
    }
}
