package org.zlenko.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zlenko.backend.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("""
            SELECT c FROM Client c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))
               OR c.phone LIKE CONCAT('%', :search, '%')
            """)
    Page<Client> searchClients(
            @Param("search") String search,
            Pageable pageable
    );

    boolean existsByEmailIgnoreCase(String email);
}
