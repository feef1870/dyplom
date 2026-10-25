package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
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
