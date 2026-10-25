package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.WorkStepRequest;
import org.zlenko.backend.dto.WorkStepResponse;
import org.zlenko.backend.entities.Order;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.OrderRepository;
import org.zlenko.backend.repositories.WorkStepRepository;

@Service
@RequiredArgsConstructor
public class WorkStepService {
    private final WorkStepRepository workStepRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<WorkStepResponse> getWorkSteps(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return workStepRepository.findAll(pageable)
                    .map(WorkStepResponse::from);
        }

        return workStepRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(WorkStepResponse::from);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public WorkStepResponse createWorkStep(WorkStepRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new NotFoundException("Order", request.orderId()));

        WorkStep step = new WorkStep();
        step.setName(request.name());
        step.setDescription(request.description());
        step.setStatus(Status.PLANNED);
        step.setOrder(order);

        return WorkStepResponse.from(workStepRepository.save(step));
    }
}
