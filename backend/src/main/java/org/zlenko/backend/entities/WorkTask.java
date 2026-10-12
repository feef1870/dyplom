package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.zlenko.backend.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Table(name = "work_tasks")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class WorkTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "(work_tasks) name is null or blank")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "status")
    @NotNull(message = "(work_tasks) status is null")
    private Status status;

    @Column(name = "unit_quantity", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "(work_tasks) unit quantity is null")
    @Min(0)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal unitQuantity;

    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "(work_tasks) price snapshot is null")
    @Min(0)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal priceSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_step_id")
    @NotNull(message = "(work_tasks) work step id is null or blank")
    private WorkStep workStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_tag_id")
    @NotNull(message = "(work_tasks) price tag id is null or blank")
    private PriceTag priceTag;

    @ManyToMany(mappedBy = "workTasks")
    private Set<Worker> workers = new HashSet<>();
}
