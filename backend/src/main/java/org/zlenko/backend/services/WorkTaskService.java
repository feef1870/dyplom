package org.zlenko.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zlenko.backend.dto.WorkTaskRequest;
import org.zlenko.backend.entities.PriceTag;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.entities.Worker;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.repositories.PriceTagRepository;
import org.zlenko.backend.repositories.WorkStepRepository;
import org.zlenko.backend.repositories.WorkTaskRepository;
import org.zlenko.backend.repositories.WorkerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkTaskService {
    private final WorkTaskRepository workTaskRepository;
    private final PriceTagRepository priceTagRepository;
    private final WorkStepRepository workStepRepository;
    private final WorkerRepository workerRepository;

    public Page<WorkTask> getWorkTasks(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return workTaskRepository.findAll(pageable);
        }

        return workTaskRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    //TODO: create custom exception for "resource not found"
    public WorkTask createWorkTask(WorkTaskRequest request) {
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

        return workTaskRepository.save(task);
    }

    //TODO: custom exception
    @Transactional
    public WorkTask assignWorkerToTask(Long workerId, Long workTaskId) {
        WorkTask task = workTaskRepository.findById(workTaskId)
                .orElseThrow(RuntimeException::new);
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(RuntimeException::new);

        if (task.getStatus() == Status.CLOSED) {
            throw new IllegalStateException("Cannot assign workers to a closed task");
        }

        if (task.getWorkers().contains(worker)) {
            return task;
        }

        task.assignWorker(worker);
        return task;
    }

    @Transactional
    public WorkTask unassignWorkerFromTask(Long taskId, Long workerId) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(RuntimeException::new);
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(RuntimeException::new);

        task.unassignWorker(worker);
        return task;
    }


}
