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
import org.zlenko.backend.exceptions.ConflictException;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.OrderRepository;
import org.zlenko.backend.repositories.WorkStepRepository;
import org.zlenko.backend.repositories.WorkTaskRepository;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkStepService {
    private final WorkStepRepository workStepRepository;
    private final OrderRepository orderRepository;
    private final WorkTaskRepository workTaskRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<WorkStepResponse> getWorkSteps(String search, int page, int size, Long orderId) {
        Pageable pageable = PageRequest.of(page, size);
        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasSearch && orderId != null) {
            return workStepRepository.findByNameContainingIgnoreCaseAndOrder_Id(search, orderId, pageable)
                    .map(WorkStepResponse::from);
        }
        if (hasSearch) {
            return workStepRepository.findByNameContainingIgnoreCase(search, pageable)
                    .map(WorkStepResponse::from);
        }
        if (orderId != null) {
            return workStepRepository.findByOrder_Id(orderId, pageable)
                    .map(WorkStepResponse::from);
        }
        return workStepRepository.findAll(pageable)
                .map(WorkStepResponse::from);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public WorkStepResponse getWorkStep(Long id) {
        return WorkStepResponse.from(workStepRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("WorkStep", id)));
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

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public WorkStepResponse startWorkStep(Long id) {
        WorkStep step = workStepRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("WorkStep", id));

        if (step.getStatus() != Status.PLANNED) {
            throw new ConflictException("Work step can only be started from PLANNED status");
        }

        step.setStatus(Status.IN_PROGRESS);
        step.setStartDate(Instant.now());
        return WorkStepResponse.from(step);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public WorkStepResponse completeWorkStep(Long id) {
        WorkStep step = workStepRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("WorkStep", id));

        if (step.getStatus() != Status.IN_PROGRESS) {
            throw new ConflictException("Work step can only be completed from IN_PROGRESS status");
        }

        if (workTaskRepository.existsByWorkStep_IdAndStatusNot(id, Status.CLOSED)) {
            throw new ConflictException("Cannot complete work step: not all tasks are closed");
        }

        step.setStatus(Status.CLOSED);
        step.setEndDate(Instant.now());
        return WorkStepResponse.from(step);
    }
}
