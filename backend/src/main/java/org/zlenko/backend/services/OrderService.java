package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.OrderRequest;
import org.zlenko.backend.dto.OrderResponse;
import org.zlenko.backend.entities.Client;
import org.zlenko.backend.entities.Order;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.ClientRepository;
import org.zlenko.backend.repositories.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<OrderResponse> getOrders(String search, int page, int size, Status status) {
        Pageable pageable = PageRequest.of(page, size);
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
}
