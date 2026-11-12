package org.zlenko.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.WorkReportPhoto;

import java.util.Optional;

public interface WorkReportPhotoRepository extends JpaRepository<WorkReportPhoto, Long> {

    Optional<WorkReportPhoto> findByIdAndWorkReport_Id(Long id, Long workReportId);
}
