package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.WorkTaskRequest;
import org.zlenko.backend.dto.WorkTaskResponse;
import org.zlenko.backend.entities.PriceTag;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.entities.Worker;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.repositories.PriceTagRepository;
import org.zlenko.backend.repositories.WorkStepRepository;
import org.zlenko.backend.repositories.WorkTaskRepository;
import org.zlenko.backend.repositories.WorkerRepository;


@Service
@RequiredArgsConstructor
public class WorkTaskService {
    private final WorkTaskRepository workTaskRepository;
    private final PriceTagRepository priceTagRepository;
    private final WorkStepRepository workStepRepository;
    private final WorkerRepository workerRepository;

    @Transactional(readOnly = true)
    public Page<WorkTaskResponse> getWorkTasks(String keycloakId, Authentication authentication, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_manager"));

        if (isManager) {
            return hasSearch
                    ? workTaskRepository.findByNameContainingIgnoreCase(search, pageable)
                    .map(WorkTaskResponse::from)
                    : workTaskRepository.findAll(pageable)
                    .map(WorkTaskResponse::from);
        }

        Worker worker = workerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(RuntimeException::new);   //TODO: custom exception

        return hasSearch
                ? workTaskRepository.findByWorkers_IdAndNameContainingIgnoreCase(worker.getId(), search, pageable)
                .map(WorkTaskResponse::from)
                : workTaskRepository.findByWorkers_Id(worker.getId(), pageable)
                .map(WorkTaskResponse::from);
    }

    //TODO: create custom exception for "resource not found"
    @Transactional
    @PreAuthorize("hasRole('manager')")
    public WorkTaskResponse createWorkTask(WorkTaskRequest request) {
        PriceTag priceTag = priceTagRepository.findById(request.priceTagId())
                .orElseThrow(RuntimeException::new);
        WorkStep workStep = workStepRepository.findById(request.workStepId())
                .orElseThrow(RuntimeException::new);

        WorkTask task = new WorkTask();
        task.setName(request.name());
        task.setDescription(request.description());
        task.setUnitQuantity(request.unitQuantity());
        task.setPriceSnapshot(priceTag.getUnitPrice());
        task.setPriceTag(priceTag);
        task.setWorkStep(workStep);
        task.setStatus(Status.PLANNED);

        return WorkTaskResponse.from(workTaskRepository.save(task));
    }

    //TODO: custom exception
    @Transactional
    @PreAuthorize("hasRole('manager')")
    public WorkTaskResponse assignWorkerToTask(Long taskId, Long workerId) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new);
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(RuntimeException::new);

        if (task.getStatus() == Status.CLOSED) {
            throw new IllegalStateException("Cannot assign workers to a closed task");
        }

        if (task.getWorkers().contains(worker)) {
            return WorkTaskResponse.from(task);
        }

        task.assignWorker(worker);
        return WorkTaskResponse.from(task);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public WorkTaskResponse unassignWorkerFromTask(Long taskId, Long workerId) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new);
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(RuntimeException::new);

        if (task.getStatus() == Status.CLOSED) {
            throw new IllegalStateException("Cannot unassign workers from a closed task");
        }

        task.unassignWorker(worker);
        return WorkTaskResponse.from(task);
    }


}
