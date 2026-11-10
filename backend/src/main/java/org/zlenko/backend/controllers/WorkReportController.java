package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.RejectWorkReportRequest;
import org.zlenko.backend.dto.UpdateWorkReportRequest;
import org.zlenko.backend.dto.WorkReportRequest;
import org.zlenko.backend.dto.WorkReportResponse;
import org.zlenko.backend.enums.ReportStatus;
import org.zlenko.backend.services.WorkReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class WorkReportController {
    private final WorkReportService workReportService;

    @GetMapping
    public Page<WorkReportResponse> getReports(
            Authentication authentication,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long stepId,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return workReportService.getReports(authentication, taskId, stepId, status, page, size);
    }

    @GetMapping("/{id}")
    public WorkReportResponse getReport(@PathVariable("id") Long id) {
        return workReportService.getReport(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkReportResponse createReport(
            @Valid @RequestBody WorkReportRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return workReportService.createReport(request, jwt);
    }

    @PutMapping("/{id}")
    public WorkReportResponse updateReport(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateWorkReportRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return workReportService.updateReport(id, request, jwt);
    }

    @PostMapping("/{id}/accept")
    public WorkReportResponse acceptReport(@PathVariable("id") Long id) {
        return workReportService.acceptReport(id);
    }

    @PostMapping("/{id}/reject")
    public WorkReportResponse rejectReport(
            @PathVariable("id") Long id,
            @Valid @RequestBody RejectWorkReportRequest request
    ) {
        return workReportService.rejectReport(id, request.feedback());
    }
}
