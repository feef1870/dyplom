package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.WorkStep;

public interface WorkStepRepository extends JpaRepository<WorkStep, Long> {
    Page<WorkStep> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
