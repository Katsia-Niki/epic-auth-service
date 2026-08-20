package by.nikiforova.epic_auth_service.client;

import by.nikiforova.epic_auth_service.dto.request.UserCreateRequestDto;
import by.nikiforova.epic_auth_service.dto.response.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private static final String INTERNAL_KEY_HEADER = "X-Internal-Key";

    private final RestClient restClient = RestClient.builder().build();

    private final String userServiceUrl;
    private final String internalKey;

    public UserServiceClient(@Value("${user.service.url}") String userServiceUrl,
                             @Value("${app.internal-key}") String internalKey) {

        this.userServiceUrl = userServiceUrl;
        this.internalKey = internalKey;
    }

    public UserResponseDto createUser(UserCreateRequestDto requestDto) {

        return restClient.post()
                .uri(userServiceUrl + "/api/users")
                .header(INTERNAL_KEY_HEADER, internalKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .body(UserResponseDto.class);

    }

    public void deleteUser(Long userId) {
        restClient.delete()
                .uri(userServiceUrl + "/api/users/{id}", userId)
                .header(INTERNAL_KEY_HEADER, internalKey)
                .retrieve()
                .toBodilessEntity();
    }
}
