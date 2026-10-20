package org.zlenko.backend.dto;

import org.zlenko.backend.entities.PriceTag;

import java.math.BigDecimal;

public record PriceTagResponse(
        Long id,
        String name,
        BigDecimal unitPrice,
        Boolean isActive
) {
    public static PriceTagResponse from(PriceTag priceTag) {
        return new PriceTagResponse(
                priceTag.getId(),
                priceTag.getName(),
                priceTag.getUnitPrice(),
                priceTag.getIsActive()
        );
    }
}
