package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.Order;
import org.zlenko.backend.enums.Status;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    @EntityGraph(attributePaths = "client")
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "client")
    Page<Order> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "client")
    Page<Order> findByStatus(Status status, Pageable pageable);

    @EntityGraph(attributePaths = "client")
    Page<Order> findByNameContainingIgnoreCaseAndStatus(String name, Status status, Pageable pageable);
}
