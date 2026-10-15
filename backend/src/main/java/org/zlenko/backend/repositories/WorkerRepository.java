package org.zlenko.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.Worker;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByKeycloakId(String keycloakId);
}
