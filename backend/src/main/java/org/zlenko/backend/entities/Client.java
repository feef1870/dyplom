package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "(clients) first name is null or blank")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "(clients) last name is null or blank")
    private String lastName;

    @Column(name = "phone")
    @Length(max = 20)
    private String phone;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "(clients) email is null or blank")
    @Email
    private String email;
}
