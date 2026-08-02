package by.nikiforova.epic_auth_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponseDto(Long id,
                              String name,
                              String surname,
                              String email,
                              LocalDate birthDate,
                              Boolean active,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}

