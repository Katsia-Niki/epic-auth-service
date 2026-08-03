package by.nikiforova.epic_auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto (@NotBlank String login,
                               @NotBlank @Size(min = 8) String password) {
}
