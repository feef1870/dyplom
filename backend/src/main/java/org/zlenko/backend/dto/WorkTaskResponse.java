package org.zlenko.backend.dto;

import org.zlenko.backend.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;

public record WorkTaskResponse(
        Long id,
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        Status status,
        BigDecimal unitQuantity,
        BigDecimal priceSnapshot,
        Long workStepId
) {
}
