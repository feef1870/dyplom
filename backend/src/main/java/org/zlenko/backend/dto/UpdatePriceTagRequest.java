package org.zlenko.backend.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record UpdatePriceTagRequest(
        String name,
        @Min(0) @Digits(integer = 8, fraction = 2) BigDecimal unitPrice,
        Boolean isActive
) {
}
