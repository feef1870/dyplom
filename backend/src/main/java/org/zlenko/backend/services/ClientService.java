package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.ClientRequest;
import org.zlenko.backend.dto.ClientResponse;
import org.zlenko.backend.entities.Client;
import org.zlenko.backend.exceptions.ConflictException;
import org.zlenko.backend.repositories.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<ClientResponse> getClients(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return clientRepository.findAll(pageable)
                    .map(ClientResponse::from);
        }

        return clientRepository.searchClients(search, pageable)
                .map(ClientResponse::from);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public ClientResponse createClient(ClientRequest request) {
        if (clientRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Client with email '" + request.email() + "' already exists");
        }

        Client client = new Client();
        client.setFirstName(request.firstName());
        client.setLastName(request.lastName());
        client.setEmail(request.email());
        client.setPhone(request.phone());

        return ClientResponse.from(clientRepository.save(client));
    }
}

