package org.zlenko.backend.dto;

import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record WorkTaskResponse(
        Long id,
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        Status status,
        BigDecimal unitQuantity,
        BigDecimal priceSnapshot,
        List<WorkerResponse> workers,
        Long workStepId
) {
    public static WorkTaskResponse from(WorkTask task) {
        return new WorkTaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStartDate(),
                task.getEndDate(),
                task.getStatus(),
                task.getUnitQuantity(),
                task.getPriceSnapshot(),
                task.getWorkers().stream().map(WorkerResponse::from).toList(),
                task.getWorkStep().getId()
        );
    }
}
