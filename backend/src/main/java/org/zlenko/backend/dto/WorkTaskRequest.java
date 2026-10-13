package org.zlenko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WorkTaskRequest(
        @NotBlank String name,
        String description,
        @NotNull BigDecimal unitQuantity,
        @NotNull Long priceTagId,
        @NotNull Long workStepId
) {
}
