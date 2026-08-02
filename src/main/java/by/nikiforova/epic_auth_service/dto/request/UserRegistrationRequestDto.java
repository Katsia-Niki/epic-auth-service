package by.nikiforova.epic_auth_service.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegistrationRequestDto (@NotBlank String name,
                                          @NotBlank String surname,
                                          @NotBlank @Email String email,
                                          @NotNull @Past LocalDate birthDate,
                                          @NotBlank String login,
                                          @NotBlank @Size(min = 8) String password){
}
