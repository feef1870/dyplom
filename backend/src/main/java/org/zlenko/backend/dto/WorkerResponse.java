package org.zlenko.backend.dto;

import org.zlenko.backend.entities.Worker;

public record WorkerResponse(
        Long id,
        String keycloakId,
        String firstName,
        String lastName,
        String email
) {
    public static WorkerResponse from(Worker w) {
        return new WorkerResponse(w.getId(),
                w.getKeycloakId(),
                w.getFirstName(),
                w.getLastName(),
                w.getEmail());
    }
}
