package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zlenko.backend.entities.Worker;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByKeycloakId(String keycloakId);

    @Query("""
            SELECT w FROM Worker w
            WHERE LOWER(w.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(w.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(w.email) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Worker> searchWorkers(@Param("search") String search, Pageable pageable);
}
