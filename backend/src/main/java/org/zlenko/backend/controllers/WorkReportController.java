package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zlenko.backend.dto.RejectWorkReportRequest;
import org.zlenko.backend.dto.UpdateWorkReportRequest;
import org.zlenko.backend.dto.WorkReportRequest;
import org.zlenko.backend.dto.WorkReportResponse;
import org.zlenko.backend.enums.ReportStatus;
import org.zlenko.backend.services.WorkReportService;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WorkReportResponse createReport(
            @Valid @RequestPart("report") WorkReportRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication authentication
    ) {
        return workReportService.createReport(request, photos, authentication);
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
    public WorkReportResponse acceptReport(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        return workReportService.acceptReport(id, authentication);
    }

    @PostMapping("/{id}/reject")
    public WorkReportResponse rejectReport(
            @PathVariable("id") Long id,
            @Valid @RequestBody RejectWorkReportRequest request,
            Authentication authentication
    ) {
        return workReportService.rejectReport(id, request.feedback(), authentication);
    }

    @GetMapping("/{id}/photos/{photoId}")
    public ResponseEntity<InputStreamResource> getReportPhoto(
            @PathVariable("id") Long id,
            @PathVariable("photoId") Long photoId,
            Authentication authentication
    ) {
        WorkReportService.ReportPhotoContent photo = workReportService.getReportPhoto(id, photoId, authentication);

        String filename = photo.originalFilename() == null || photo.originalFilename().isBlank()
                ? "photo-" + photoId
                : photo.originalFilename();
        MediaType contentType = photo.contentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(photo.contentType());

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=300")
                .body(new InputStreamResource(photo.stream()));
    }
}
