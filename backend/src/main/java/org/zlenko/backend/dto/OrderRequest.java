package org.zlenko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotBlank String name,
        String description,
        @NotNull Long clientId
) {
}
