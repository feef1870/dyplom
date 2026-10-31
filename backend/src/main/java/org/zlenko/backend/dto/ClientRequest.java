package org.zlenko.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ClientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Length(max = 20) String phone,
        @NotBlank @Email String email
) {
}
