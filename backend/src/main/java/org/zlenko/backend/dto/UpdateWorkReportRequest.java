package org.zlenko.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateWorkReportRequest(
        @NotBlank String comment
) {
}
