package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zlenko.backend.repositories.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
}
