package by.nikiforova.epic_auth_service.dto.response;

public record JwtResponseDto (String accessToken,
                              String refreshToken) {
}
