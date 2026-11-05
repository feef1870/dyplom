package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.OrderRequest;
import org.zlenko.backend.dto.OrderResponse;
import org.zlenko.backend.enums.Status;
import org.zlenko.backend.services.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Page<OrderResponse> getOrders(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Status status
    ) {
        return orderService.getOrders(search, page, size, status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable("id") Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping("/{id}/start")
    public OrderResponse startOrder(@PathVariable("id") Long id) {
        return orderService.startOrder(id);
    }

    @PostMapping("/{id}/complete")
    public OrderResponse completeOrder(@PathVariable("id") Long id) {
        return orderService.completeOrder(id);
    }
}
