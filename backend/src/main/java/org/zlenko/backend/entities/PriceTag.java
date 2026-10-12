package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Table(name = "price_tags")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PriceTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "(price_tags) name is null or blank")
    private String name;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "(price_tags) unit price is null")
    @Min(0)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal unitPrice;

    @Column(name = "is_active", nullable = false)
    @NotNull(message = "(price_tags) is active is null")
    @ColumnDefault("true")
    private Boolean isActive;
}
