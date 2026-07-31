package by.nikiforova.epic_auth_service.integration;

import by.nikiforova.epic_auth_service.dto.request.CredentialRequestDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CredentialControllerIntegrationTest {

    private static final CredentialRequestDto CREDENTIAL_REQUEST =
            new CredentialRequestDto(1L, "liza", "password123", Role.USER);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private CredentialRepository credentialRepository;

    @BeforeEach
    void setUp() {
        credentialRepository.deleteAll();
    }

    @Test
    void shouldCreateCredentialsAndSaveInDatabase() throws Exception {
        mockMvc.perform(post("/api/credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREDENTIAL_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("liza"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("USER"));

        assertThat(credentialRepository.count()).isEqualTo(1);
        assertThat(credentialRepository.existsByLogin("liza")).isTrue();
    }

    @Test
    void shouldReturnConflictWhenLoginAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CREDENTIAL_REQUEST)));

        mockMvc.perform(post("/api/credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREDENTIAL_REQUEST)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Login already exists: liza"));

        assertThat(credentialRepository.count()).isEqualTo(1);
    }
}
