package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.OrderRequest;
import org.zlenko.backend.dto.OrderResponse;
import org.zlenko.backend.entities.Client;
import org.zlenko.backend.entities.Order;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.exceptions.ConflictException;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.ClientRepository;
import org.zlenko.backend.repositories.OrderRepository;
import org.zlenko.backend.repositories.WorkStepRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final WorkStepRepository workStepRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<OrderResponse> getOrders(String search, int page, int size, Status status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasSearch && status != null) {
            return orderRepository.findByNameContainingIgnoreCaseAndStatus(search, status, pageable)
                    .map(OrderResponse::from);
        }
        if (hasSearch) {
            return orderRepository.findByNameContainingIgnoreCase(search, pageable)
                    .map(OrderResponse::from);
        }
        if (status != null) {
            return orderRepository.findByStatus(status, pageable)
                    .map(OrderResponse::from);
        }

        return orderRepository.findAll(pageable)
                .map(OrderResponse::from);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public OrderResponse createOrder(OrderRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new NotFoundException("Client", request.clientId()));

        Order order = new Order();
        order.setClient(client);
        order.setDescription(request.description());
        order.setName(request.name());
        order.setStatus(Status.PLANNED);

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public OrderResponse getOrder(Long id) {
        return OrderResponse.from(orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order", id))
        );
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public OrderResponse startOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order", id));

        if (order.getStatus() != Status.PLANNED) {
            throw new ConflictException("Order can only be started from PLANNED status");
        }

        order.setStatus(Status.IN_PROGRESS);
        return OrderResponse.from(order);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order", id));

        if (order.getStatus() != Status.IN_PROGRESS) {
            throw new ConflictException("Order can only be completed from IN_PROGRESS status");
        }

        if (workStepRepository.existsByOrder_IdAndStatusNot(id, Status.CLOSED)) {
            throw new ConflictException("Cannot complete order: not all work steps are closed");
        }

        order.setStatus(Status.CLOSED);
        return OrderResponse.from(order);
    }
}
