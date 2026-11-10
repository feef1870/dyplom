package org.zlenko.backend.dto;

import org.zlenko.backend.entities.WorkReport;
import org.zlenko.backend.entities.Worker;
import org.zlenko.backend.enums.ReportStatus;

import java.time.Instant;

public record WorkReportResponse(
        Long id,
        Instant createdAt,
        String comment,
        ReportStatus status,
        String feedback,
        Instant reviewedAt,
        Long workTaskId,
        Long workStepId,
        String workTaskName,
        String workStepName,
        Long authorId,
        String authorFirstName,
        String authorLastName
) {
    public static WorkReportResponse from(WorkReport report) {
        Worker author = report.getAuthor();
        return new WorkReportResponse(
                report.getId(),
                report.getCreatedAt(),
                report.getComment(),
                report.getStatus(),
                report.getFeedback(),
                report.getReviewedAt(),
                report.getWorkTask() != null ? report.getWorkTask().getId() : null,
                report.getWorkStep() != null ? report.getWorkStep().getId() : null,
                report.getWorkTask() != null ? report.getWorkTask().getName() : null,
                report.getWorkStep() != null ? report.getWorkStep().getName() : null,
                author.getId(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}
