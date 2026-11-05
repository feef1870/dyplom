package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.WorkStep;
import org.zlenko.backend.enums.Status;

public interface WorkStepRepository extends JpaRepository<WorkStep, Long> {
    Page<WorkStep> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<WorkStep> findByOrder_Id(Long orderId, Pageable pageable);
    Page<WorkStep> findByNameContainingIgnoreCaseAndOrder_Id(String name, Long orderId, Pageable pageable);
    boolean existsByOrder_IdAndStatusNot(Long orderId, Status status);
}
