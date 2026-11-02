package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.WorkerResponse;
import org.zlenko.backend.entities.Worker;
import org.zlenko.backend.repositories.WorkerRepository;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<WorkerResponse> getWorkers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return workerRepository.findAll(pageable)
                    .map(WorkerResponse::from);
        }

        return workerRepository.searchWorkers(search, pageable)
                .map(WorkerResponse::from);
    }

    @Transactional
    public WorkerResponse getOrCreateWorker(Jwt jwt) {
        return WorkerResponse.from(workerRepository.findByKeycloakId(jwt.getSubject())
                .map(worker -> refreshProfile(worker, jwt))
                .orElseGet(() -> createFromJwt(jwt)));
    }

    private Worker createFromJwt(Jwt jwt) {
        Worker worker = new Worker();
        worker.setKeycloakId(jwt.getSubject());
        worker.setFirstName(jwt.getClaimAsString("given_name"));
        worker.setLastName(jwt.getClaimAsString("family_name"));
        worker.setEmail(jwt.getClaimAsString("email"));
        return workerRepository.save(worker);
    }

    private Worker refreshProfile(Worker worker, Jwt jwt) {
        worker.setFirstName(jwt.getClaimAsString("given_name"));
        worker.setLastName(jwt.getClaimAsString("family_name"));
        worker.setEmail(jwt.getClaimAsString("email"));
        return worker;
    }
}
