package org.zlenko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkStepRequest(
        @NotBlank String name,
        String description,
        @NotNull Long orderId
) {
}
