package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zlenko.backend.dto.WorkTaskRequest;
import org.zlenko.backend.entities.PriceTag;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.repositories.PriceTagRepository;
import org.zlenko.backend.repositories.WorkStepRepository;
import org.zlenko.backend.repositories.WorkTaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkTaskService {
    private final WorkTaskRepository workTaskRepository;
    private final PriceTagRepository priceTagRepository;
    private final WorkStepRepository workStepRepository;

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


}
