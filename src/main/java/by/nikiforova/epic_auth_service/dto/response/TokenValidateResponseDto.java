package by.nikiforova.epic_auth_service.dto.response;

import by.nikiforova.epic_auth_service.entity.Role;

public record TokenValidateResponseDto (boolean valid,
                                        Long userId,
                                        Role role,
                                        String login) {
}
