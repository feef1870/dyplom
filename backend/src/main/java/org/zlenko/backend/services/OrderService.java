package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zlenko.backend.dto.OrderRequest;
import org.zlenko.backend.entities.Client;
import org.zlenko.backend.entities.Order;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.repositories.ClientRepository;
import org.zlenko.backend.repositories.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public Page<Order> getOrders(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return orderRepository.findAll(pageable);
        }

        return orderRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    //TODO: custom exception
    public Order createOrder(OrderRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(RuntimeException::new);

        Order order = new Order();
        order.setClient(client);
        order.setDescription(request.description());
        order.setName(request.name());
        order.setStatus(Status.PLANNED);

        return orderRepository.save(order);
    }
}
