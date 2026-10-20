package org.zlenko.backend.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceTagRequest(
        @NotBlank String name,
        @NotNull @Min(0) @Digits(integer = 8, fraction = 2) BigDecimal unitPrice
) {
}
