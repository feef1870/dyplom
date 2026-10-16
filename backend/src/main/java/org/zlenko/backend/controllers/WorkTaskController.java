package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.WorkTaskRequest;
import org.zlenko.backend.dto.WorkTaskResponse;
import org.zlenko.backend.services.WorkTaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class WorkTaskController {
    private final WorkTaskService workTaskService;

    @GetMapping
    public Page<WorkTaskResponse> getWorkTasks(
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return workTaskService.getWorkTasks(jwt.getSubject(), authentication, search, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkTaskResponse createWorkTask(
            @Valid @RequestBody WorkTaskRequest request) {

        return workTaskService.createWorkTask(request);
    }

    @DeleteMapping("/{taskId}/workers/{workerId}")
    public WorkTaskResponse unassignWorkerFromTask(
            @PathVariable("taskId") Long taskId,
            @PathVariable("workerId") Long workerId) {

        return workTaskService.unassignWorkerFromTask(taskId, workerId);
    }

    @PutMapping("/{taskId}/workers/{workerId}")
    public WorkTaskResponse assignWorkerToTask(
            @PathVariable("taskId") Long taskId,
            @PathVariable("workerId") Long workerId) {

        return workTaskService.assignWorkerToTask(taskId, workerId);
    }

}
