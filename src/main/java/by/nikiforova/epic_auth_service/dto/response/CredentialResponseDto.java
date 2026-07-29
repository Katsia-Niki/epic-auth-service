package by.nikiforova.epic_auth_service.dto.response;

import by.nikiforova.epic_auth_service.entity.Role;

import java.time.LocalDateTime;

public record CredentialResponseDto (Long id,
                                     Long userId,
                                     String login,
                                     Role role,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
}
