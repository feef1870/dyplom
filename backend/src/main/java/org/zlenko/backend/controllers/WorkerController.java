package org.zlenko.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zlenko.backend.dto.WorkerResponse;
import org.zlenko.backend.services.WorkerService;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping
    public Page<WorkerResponse> getWorkers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return workerService.getWorkers(search, page, size);
    }

    @GetMapping("/me")
    public WorkerResponse me(@AuthenticationPrincipal Jwt jwt) {
        return workerService.getOrCreateWorker(jwt);
    }
}
