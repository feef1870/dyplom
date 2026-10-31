package org.zlenko.backend.dto;

import org.zlenko.backend.entities.Client;

public record ClientResponse(
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getPhone(),
                client.getEmail()
        );
    }
}
