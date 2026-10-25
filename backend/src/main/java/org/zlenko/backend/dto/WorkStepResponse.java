package org.zlenko.backend.dto;

import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.enums.Status;

import java.time.Instant;

public record WorkStepResponse(
        Long id,
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        Status status,
        Long orderId
) {
    public static WorkStepResponse from(WorkStep workStep) {
        return new WorkStepResponse(
                workStep.getId(),
                workStep.getName(),
                workStep.getDescription(),
                workStep.getStartDate(),
                workStep.getEndDate(),
                workStep.getStatus(),
                workStep.getOrder().getId()
        );
    }
}
