package by.nikiforova.epic_auth_service.controller;

import by.nikiforova.epic_auth_service.dto.request.LoginRequestDto;
import by.nikiforova.epic_auth_service.dto.request.TokenRequestDto;
import by.nikiforova.epic_auth_service.dto.response.JwtResponseDto;
import by.nikiforova.epic_auth_service.dto.response.TokenValidateResponseDto;
import by.nikiforova.epic_auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticate (@Valid @RequestBody LoginRequestDto loginRequestDto) {
        JwtResponseDto jwtResponseDto = authService.authenticate(loginRequestDto);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidateResponseDto> validate(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.validate(tokenRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.refreshToken(tokenRequestDto));
    }

}
