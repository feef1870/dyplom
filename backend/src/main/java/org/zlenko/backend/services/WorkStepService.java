package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zlenko.backend.dto.WorkStepRequest;
import org.zlenko.backend.entities.Order;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.repositories.OrderRepository;
import org.zlenko.backend.repositories.WorkStepRepository;

@Service
@RequiredArgsConstructor
public class WorkStepService {
    private final WorkStepRepository workStepRepository;
    private final OrderRepository orderRepository;

    public Page<WorkStep> getWorkSteps(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return workStepRepository.findAll(pageable);
        }

        return workStepRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    //TODO: custom exception
    public WorkStep createWorkStep(WorkStepRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(RuntimeException::new);

        WorkStep step = new WorkStep();
        step.setName(request.name());
        step.setDescription(request.description());
        step.setStatus(Status.PLANNED);
        step.setOrder(order);

        return workStepRepository.save(step);
    }
}
