package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.WorkTask;
import org.zlenko.backend.enums.Status;

import java.util.Optional;

public interface WorkTaskRepository extends JpaRepository<WorkTask, Long> {
    @Override
    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByWorkers_Id(Long workerId, Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByWorkers_IdAndNameContainingIgnoreCase(Long workerId, String name, Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByWorkStep_Id(Long workStepId, Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByNameContainingIgnoreCaseAndWorkStep_Id(String name, Long workStepId, Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByWorkers_IdAndWorkStep_Id(Long workerId, Long workStepId, Pageable pageable);

    @EntityGraph(attributePaths = "workStep")
    Page<WorkTask> findByWorkers_IdAndWorkStep_IdAndNameContainingIgnoreCase(Long workerId, String name, Long workStepId, Pageable pageable);

    Optional<WorkTask> findByIdAndWorkers_Id(Long id, Long workerId);
    boolean existsByWorkStep_IdAndStatusNot(Long workStepId, Status status);
}
