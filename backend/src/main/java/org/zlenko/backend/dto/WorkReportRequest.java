package org.zlenko.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record WorkReportRequest(
        @NotBlank String comment,
        Long workTaskId,
        Long workStepId
) {
}
