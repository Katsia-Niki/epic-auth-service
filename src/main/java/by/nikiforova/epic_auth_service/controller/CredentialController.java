package by.nikiforova.epic_auth_service.controller;

import by.nikiforova.epic_auth_service.dto.request.CredentialRequestDto;
import by.nikiforova.epic_auth_service.dto.response.CredentialResponseDto;
import by.nikiforova.epic_auth_service.service.CredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @PostMapping
    public ResponseEntity<CredentialResponseDto> save(@Valid @RequestBody CredentialRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(credentialService.save(dto));
    }


}
