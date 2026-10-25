package org.zlenko.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zlenko.backend.dto.WorkerResponse;
import org.zlenko.backend.services.WorkerService;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping("/me")
    public WorkerResponse me(@AuthenticationPrincipal Jwt jwt) {
        return workerService.getOrCreateWorker(jwt);
    }
}
