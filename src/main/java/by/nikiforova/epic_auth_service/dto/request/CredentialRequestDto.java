package by.nikiforova.epic_auth_service.dto.request;

import by.nikiforova.epic_auth_service.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CredentialRequestDto(@NotNull Long userId,
                                   @NotBlank String login,
                                   @NotBlank @Size(min = 8) String password,
                                   @NotNull Role role) {}
