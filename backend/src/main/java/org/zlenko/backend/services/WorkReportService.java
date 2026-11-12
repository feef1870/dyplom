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
import org.springframework.web.multipart.MultipartFile;
import org.zlenko.backend.dto.UpdateWorkReportRequest;
import org.zlenko.backend.dto.WorkReportRequest;
import org.zlenko.backend.dto.WorkReportResponse;
import org.zlenko.backend.entities.WorkReport;
import org.zlenko.backend.entities.WorkReportPhoto;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.entities.Worker;
import org.zlenko.backend.enums.ReportStatus;
import org.zlenko.backend.exceptions.BadRequestException;
import org.zlenko.backend.exceptions.ConflictException;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.WorkReportPhotoRepository;
import org.zlenko.backend.repositories.WorkReportRepository;
import org.zlenko.backend.repositories.WorkStepRepository;
import org.zlenko.backend.repositories.WorkTaskRepository;
import org.zlenko.backend.repositories.WorkerRepository;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkReportService {
    private static final int MAX_PHOTOS = 10;
    private static final Map<String, String> PHOTO_EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final WorkReportRepository workReportRepository;
    private final WorkReportPhotoRepository workReportPhotoRepository;
    private final WorkerRepository workerRepository;
    private final WorkTaskRepository workTaskRepository;
    private final WorkStepRepository workStepRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public Page<WorkReportResponse> getReports(Authentication authentication, Long taskId, Long stepId, ReportStatus status, int page, int size) {
        if (status != null) {
            boolean isManager = hasRole(authentication, "manager");
            boolean isSupervisor = hasRole(authentication, "supervisor");

            if (!isManager && !isSupervisor) {
                throw new AccessDeniedException("Only supervisors and managers can list reports by status");
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
            if (isManager && isSupervisor) {
                return workReportRepository.findByStatus(status, pageable)
                        .map(WorkReportResponse::from);
            }
            if (isManager) {
                return workReportRepository.findByStatusAndWorkStepIsNotNull(status, pageable)
                        .map(WorkReportResponse::from);
            }
            return workReportRepository.findByStatusAndWorkTaskIsNotNull(status, pageable)
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
    public WorkReportResponse createReport(WorkReportRequest request, List<MultipartFile> photos, Authentication authentication) {
        if ((request.workTaskId() == null) == (request.workStepId() == null)) {
            throw new ConflictException("Report must target exactly one of work task or work step");
        }

        String keycloakId = ((Jwt) authentication.getPrincipal()).getSubject();
        Worker author = workerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Worker", keycloakId));

        WorkTask task = null;
        WorkStep step = null;
        if (request.workTaskId() != null) {
            task = workTaskRepository.findById(request.workTaskId())
                    .orElseThrow(() -> new NotFoundException("Task", request.workTaskId()));
        } else {
            step = workStepRepository.findById(request.workStepId())
                    .orElseThrow(() -> new NotFoundException("WorkStep", request.workStepId()));
        }

        authorizeReportCreation(authentication, author, task, step);

        List<MultipartFile> files = photos == null ? List.of() : photos;
        validatePhotos(files);

        WorkReport report = new WorkReport();
        report.setComment(request.comment());
        report.setStatus(ReportStatus.PENDING);
        report.setAuthor(author);
        report.setWorkTask(task);
        report.setWorkStep(step);
        report = workReportRepository.save(report);

        List<String> uploadedKeys = new ArrayList<>();
        List<WorkReportPhoto> savedPhotos = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String contentType = normalizeContentType(file.getContentType());
                String key = "reports/" + report.getId() + "/" + UUID.randomUUID() + "." + PHOTO_EXTENSIONS.get(contentType);

                try (InputStream stream = file.getInputStream()) {
                    storageService.upload(key, stream, file.getSize(), contentType);
                }
                uploadedKeys.add(key);

                WorkReportPhoto photo = new WorkReportPhoto();
                photo.setWorkReport(report);
                photo.setStorageKey(key);
                photo.setOriginalFilename(safeFilename(file.getOriginalFilename()));
                photo.setContentType(contentType);
                savedPhotos.add(workReportPhotoRepository.save(photo));
            }
        } catch (Exception e) {
            uploadedKeys.forEach(this::deleteQuietly);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Could not upload report photo", e);
        }

        report.getPhotos().addAll(savedPhotos);
        return WorkReportResponse.from(report);
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
    @PreAuthorize("hasAnyRole('manager', 'supervisor')")
    public WorkReportResponse acceptReport(Long id, Authentication authentication) {
        WorkReport report = workReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report", id));

        authorizeReview(authentication, report);

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ConflictException("Report can only be accepted when PENDING");
        }

        report.setStatus(ReportStatus.ACCEPTED);
        report.setReviewedAt(Instant.now());
        return WorkReportResponse.from(report);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('manager', 'supervisor')")
    public WorkReportResponse rejectReport(Long id, String feedback, Authentication authentication) {
        WorkReport report = workReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report", id));

        authorizeReview(authentication, report);

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ConflictException("Report can only be rejected when PENDING");
        }

        report.setStatus(ReportStatus.REJECTED);
        report.setFeedback(feedback);
        report.setReviewedAt(Instant.now());
        return WorkReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public ReportPhotoContent getReportPhoto(Long reportId, Long photoId, Authentication authentication) {
        WorkReport report = workReportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report", reportId));
        WorkReportPhoto photo = workReportPhotoRepository.findByIdAndWorkReport_Id(photoId, reportId)
                .orElseThrow(() -> new NotFoundException("Report photo", photoId));

        authorizePhotoAccess(authentication, report);

        return new ReportPhotoContent(
                storageService.download(photo.getStorageKey()),
                photo.getContentType(),
                photo.getOriginalFilename()
        );
    }

    private void authorizeReportCreation(Authentication authentication, Worker author, WorkTask task, WorkStep step) {
        if (hasRole(authentication, "manager")) {
            return;
        }
        if (step != null) {
            if (hasRole(authentication, "supervisor")) {
                return;
            }
            throw new AccessDeniedException("Only supervisors and managers can create step reports");
        }
        if (hasRole(authentication, "supervisor")) {
            throw new AccessDeniedException("Supervisors cannot create task reports");
        }
        if (workTaskRepository.findByIdAndWorkers_Id(task.getId(), author.getId()).isEmpty()) {
            throw new AccessDeniedException("Workers can only create reports for tasks they are assigned to");
        }
    }

    private void authorizeReview(Authentication authentication, WorkReport report) {
        if (report.getWorkStep() != null) {
            if (!hasRole(authentication, "manager")) {
                throw new AccessDeniedException("Step reports can only be reviewed by managers");
            }
            return;
        }
        if (!hasRole(authentication, "supervisor")) {
            throw new AccessDeniedException("Task reports can only be reviewed by supervisors");
        }
    }

    private void authorizePhotoAccess(Authentication authentication, WorkReport report) {
        if (hasRole(authentication, "manager") || hasRole(authentication, "supervisor")) {
            return;
        }

        WorkTask task = report.getWorkTask();
        if (task == null) {
            throw new AccessDeniedException("Workers cannot view photos of step reports");
        }

        String keycloakId = ((Jwt) authentication.getPrincipal()).getSubject();
        Worker worker = workerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Worker", keycloakId));

        if (workTaskRepository.findByIdAndWorkers_Id(task.getId(), worker.getId()).isEmpty()) {
            throw new AccessDeniedException("Workers can only view photos of their own tasks");
        }
    }

    private void validatePhotos(List<MultipartFile> photos) {
        if (photos.size() > MAX_PHOTOS) {
            throw new BadRequestException("A report can have at most " + MAX_PHOTOS + " photos");
        }
        for (MultipartFile photo : photos) {
            if (photo.isEmpty()) {
                throw new BadRequestException("Report photos must not be empty");
            }
            if (normalizeContentType(photo.getContentType()) == null) {
                throw new BadRequestException("Unsupported photo type: " + photo.getContentType());
            }
        }
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        if (normalized.equals("image/jpg")) {
            normalized = "image/jpeg";
        }
        return PHOTO_EXTENSIONS.containsKey(normalized) ? normalized : null;
    }

    private String safeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "photo";
        }
        String filename = originalFilename.replace('\\', '/');
        int slash = filename.lastIndexOf('/');
        return slash >= 0 ? filename.substring(slash + 1) : filename;
    }

    private void deleteQuietly(String key) {
        try {
            storageService.delete(key);
        } catch (RuntimeException ignored) {

        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public record ReportPhotoContent(InputStream stream, String contentType, String originalFilename) {
    }
}
