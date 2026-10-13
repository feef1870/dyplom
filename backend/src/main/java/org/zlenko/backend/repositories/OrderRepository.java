package org.zlenko.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
