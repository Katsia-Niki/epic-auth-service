package by.nikiforova.epic_auth_service.service;

import by.nikiforova.epic_auth_service.dto.request.CredentialRequestDto;
import by.nikiforova.epic_auth_service.dto.response.CredentialResponseDto;
import by.nikiforova.epic_auth_service.entity.Credential;
import by.nikiforova.epic_auth_service.exception.CredentialAlreadyExistsException;
import by.nikiforova.epic_auth_service.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CredentialResponseDto save(CredentialRequestDto credentialRequestDto) {

        if (credentialRepository.existsByLogin(credentialRequestDto.login())) {
            throw new CredentialAlreadyExistsException("Login already exists: " + credentialRequestDto.login());
        }

        if (credentialRepository.existsByUserId(credentialRequestDto.userId())) {
            throw new CredentialAlreadyExistsException("UserId already exists: " + credentialRequestDto.userId());
        }

        Credential credential = Credential.builder()
                .userId(credentialRequestDto.userId())
                .login(credentialRequestDto.login())
                .passwordHash(passwordEncoder.encode(credentialRequestDto.password()))
                .role(credentialRequestDto.role())
                .build();

        Credential savedCredential = credentialRepository.save(credential);

        return new CredentialResponseDto(savedCredential.getId(), savedCredential.getUserId(),
                savedCredential.getLogin(), savedCredential.getRole(), savedCredential.getCreatedAt(),
                savedCredential.getUpdatedAt());
    }


}
