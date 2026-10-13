package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.WorkTask;

public interface WorkTaskRepository extends JpaRepository<WorkTask, Long> {
    Page<WorkTask> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
