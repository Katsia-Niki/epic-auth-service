package by.nikiforova.epic_auth_service.client;

import by.nikiforova.epic_auth_service.dto.request.UserCreateRequestDto;
import by.nikiforova.epic_auth_service.dto.response.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {
    private final RestClient restClient = RestClient.builder().build();

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserResponseDto createUser(UserCreateRequestDto requestDto) {

        return restClient.post()
                .uri(userServiceUrl + "/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .body(UserResponseDto.class);

    }
}
