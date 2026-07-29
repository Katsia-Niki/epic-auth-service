package by.nikiforova.epic_auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRequestDto (@NotBlank String token) {
}
