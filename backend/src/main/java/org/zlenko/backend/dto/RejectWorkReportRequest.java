package org.zlenko.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectWorkReportRequest(
        @NotBlank String feedback
) {
}
