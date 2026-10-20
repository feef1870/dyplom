package org.zlenko.backend.dto;

import org.zlenko.backend.entities.Order;
import org.zlenko.backend.enums.Status;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String name,
        String description,
        Instant createdAt,
        Status status,
        Long clientId
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getClient().getId()
        );
    }
}
