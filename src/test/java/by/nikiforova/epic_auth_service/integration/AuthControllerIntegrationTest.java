package by.nikiforova.epic_auth_service.integration;

import by.nikiforova.epic_auth_service.dto.request.CredentialRequestDto;
import by.nikiforova.epic_auth_service.dto.request.LoginRequestDto;
import by.nikiforova.epic_auth_service.entity.Role;
import by.nikiforova.epic_auth_service.repository.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    private static final CredentialRequestDto CREDENTIAL_REQUEST =
            new CredentialRequestDto(1L, "liza", "password123", Role.USER);

    private static final LoginRequestDto LOGIN_REQUEST =
            new LoginRequestDto("liza", "password123");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private CredentialRepository credentialRepository;

    @BeforeEach
    void setUp() throws Exception {
        credentialRepository.deleteAll();

        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CREDENTIAL_REQUEST)));
    }

    @Test
    void shouldLoginAndReturnTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenWrongPassword() throws Exception {
        LoginRequestDto wrongPasswordRequest = new LoginRequestDto("liza", "wrong-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid login or password"));
    }
}
