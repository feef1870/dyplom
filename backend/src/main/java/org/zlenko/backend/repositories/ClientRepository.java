package org.zlenko.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
