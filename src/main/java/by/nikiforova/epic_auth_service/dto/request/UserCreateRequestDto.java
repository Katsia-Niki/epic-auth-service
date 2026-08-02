package by.nikiforova.epic_auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserCreateRequestDto (@NotBlank String name,
                                    @NotBlank String surname,
                                    @NotBlank @Email String email,
                                    @NotNull @Past LocalDate birthDate) {
}
