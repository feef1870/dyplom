package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.WorkStepRequest;
import org.zlenko.backend.dto.WorkStepResponse;
import org.zlenko.backend.services.WorkStepService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/steps")
public class WorkStepController {
    private final WorkStepService workStepService;

    @GetMapping
    public Page<WorkStepResponse> getWorkSteps(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long orderId
    ) {
        return workStepService.getWorkSteps(search, page, size, orderId);
    }

    @GetMapping("/{id}")
    public WorkStepResponse getWorkStep(@PathVariable("id") Long id) {
        return workStepService.getWorkStep(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkStepResponse createWorkStep(
            @Valid @RequestBody WorkStepRequest request
    ) {
        return workStepService.createWorkStep(request);
    }

    @PostMapping("/{id}/start")
    public WorkStepResponse startWorkStep(@PathVariable("id") Long id) {
        return workStepService.startWorkStep(id);
    }

    @PostMapping("/{id}/complete")
    public WorkStepResponse completeWorkStep(@PathVariable("id") Long id) {
        return workStepService.completeWorkStep(id);
    }
}
