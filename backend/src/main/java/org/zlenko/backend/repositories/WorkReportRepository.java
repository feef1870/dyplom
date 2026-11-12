package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.WorkReport;
import org.zlenko.backend.enums.ReportStatus;

public interface WorkReportRepository extends JpaRepository<WorkReport, Long> {

    @Override
    @EntityGraph(attributePaths = {"author", "workTask", "workStep"})
    Page<WorkReport> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"author", "workTask", "workStep"})
    Page<WorkReport> findByWorkTask_Id(Long workTaskId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "workTask", "workStep"})
    Page<WorkReport> findByWorkStep_Id(Long workStepId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "workTask", "workStep"})
    Page<WorkReport> findByStatus(ReportStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "workTask", "workStep"})
    Page<WorkReport> findByStatusAndWorkTaskIsNotNull(ReportStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "workTask", "workStep"})
    Page<WorkReport> findByStatusAndWorkStepIsNotNull(ReportStatus status, Pageable pageable);

    boolean existsByWorkTask_Id(Long workTaskId);

    boolean existsByWorkTask_IdAndStatusNot(Long workTaskId, ReportStatus status);

    boolean existsByWorkStep_IdAndStatusNot(Long workStepId, ReportStatus status);
}
