package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "workers")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    @NotBlank(message = "(workers) keycloak id is null or blank")
    private String keycloakId;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "(workers) first name is null or blank")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "(workers) last name is null or blank")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    @Email
    @NotBlank(message = "(workers) email is null or blank")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "workers_assigned",
            joinColumns = @JoinColumn(name = "worker_id"),
            inverseJoinColumns = @JoinColumn(name = "work_task_id")
    )
    private Set<WorkTask> workTasks = new HashSet<>();
}
