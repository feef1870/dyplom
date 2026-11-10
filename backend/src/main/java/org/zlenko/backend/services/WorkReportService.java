package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.UpdateWorkReportRequest;
import org.zlenko.backend.dto.WorkReportRequest;
import org.zlenko.backend.dto.WorkReportResponse;
import org.zlenko.backend.entities.WorkReport;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.entities.Worker;
import org.zlenko.backend.enums.ReportStatus;
import org.zlenko.backend.exceptions.ConflictException;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.WorkReportRepository;
import org.zlenko.backend.repositories.WorkStepRepository;
import org.zlenko.backend.repositories.WorkTaskRepository;
import org.zlenko.backend.repositories.WorkerRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkReportService {
    private final WorkReportRepository workReportRepository;
    private final WorkerRepository workerRepository;
    private final WorkTaskRepository workTaskRepository;
    private final WorkStepRepository workStepRepository;

    @Transactional(readOnly = true)
    public Page<WorkReportResponse> getReports(Authentication authentication, Long taskId, Long stepId, ReportStatus status, int page, int size) {
        if (status != null) {
            boolean isSupervisor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_supervisor"));
            if (!isSupervisor) {
                throw new AccessDeniedException("Only supervisors can list reports by status");
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
            return workReportRepository.findByStatus(status, pageable)
                    .map(WorkReportResponse::from);
        }

        Pageable pageable = PageRequest.of(page, size);

        if (taskId != null) {
            return workReportRepository.findByWorkTask_Id(taskId, pageable)
                    .map(WorkReportResponse::from);
        }
        if (stepId != null) {
            return workReportRepository.findByWorkStep_Id(stepId, pageable)
                    .map(WorkReportResponse::from);
        }
        return workReportRepository.findAll(pageable)
                .map(WorkReportResponse::from);
    }

    @Transactional(readOnly = true)
    public WorkReportResponse getReport(Long id) {
        return WorkReportResponse.from(workReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report", id)));
    }

    @Transactional
    public WorkReportResponse createReport(WorkReportRequest request, Jwt jwt) {
        if ((request.workTaskId() == null) == (request.workStepId() == null)) {
            throw new ConflictException("Report must target exactly one of work task or work step");
        }

        Worker author = workerRepository.findByKeycloakId(jwt.getSubject())
                .orElseThrow(() -> new NotFoundException("Worker", jwt.getSubject()));

        WorkReport report = new WorkReport();
        report.setComment(request.comment());
        report.setStatus(ReportStatus.PENDING);
        report.setAuthor(author);

        if (request.workTaskId() != null) {
            WorkTask task = workTaskRepository.findById(request.workTaskId())
                    .orElseThrow(() -> new NotFoundException("Task", request.workTaskId()));
            report.setWorkTask(task);
        } else {
            WorkStep step = workStepRepository.findById(request.workStepId())
                    .orElseThrow(() -> new NotFoundException("WorkStep", request.workStepId()));
            report.setWorkStep(step);
        }

        return WorkReportResponse.from(workReportRepository.save(report));
    }

    @Transactional
    public WorkReportResponse updateReport(Long id, UpdateWorkReportRequest request, Jwt jwt) {
        WorkReport report = workReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report", id));

        Worker current = workerRepository.findByKeycloakId(jwt.getSubject())
                .orElseThrow(() -> new NotFoundException("Worker", jwt.getSubject()));

        if (!report.getAuthor().getId().equals(current.getId())) {
            throw new ConflictException("Only the report author can edit it");
        }

        if (report.getStatus() != ReportStatus.REJECTED) {
            throw new ConflictException("Only rejected reports can be edited");
        }

        report.setComment(request.comment());
        report.setStatus(ReportStatus.PENDING);
        report.setFeedback(null);
        report.setReviewedAt(null);
        return WorkReportResponse.from(report);
    }

    @Transactional
    @PreAuthorize("hasRole('supervisor')")
    public WorkReportResponse acceptReport(Long id) {
        WorkReport report = workReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report", id));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ConflictException("Report can only be accepted when PENDING");
        }

        report.setStatus(ReportStatus.ACCEPTED);
        report.setReviewedAt(Instant.now());
        return WorkReportResponse.from(report);
    }

    @Transactional
    @PreAuthorize("hasRole('supervisor')")
    public WorkReportResponse rejectReport(Long id, String feedback) {
        WorkReport report = workReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report", id));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ConflictException("Report can only be rejected when PENDING");
        }

        report.setStatus(ReportStatus.REJECTED);
        report.setFeedback(feedback);
        report.setReviewedAt(Instant.now());
        return WorkReportResponse.from(report);
    }
}
