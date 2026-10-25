package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Table(name = "work_reports")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class WorkReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "(work reports) created at is null")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "comment", nullable = false)
    @NotBlank(message = "(work reports) comment is null or blank")
    private String comment;

    @Column(name = "is_accepted", nullable = false)
    @NotNull(message = "(work reports) is accepted is null")
    private Boolean isAccepted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_task_id")
    private WorkTask workTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_step_id")
    private WorkStep workStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @NotNull(message = "(work reports) author id is null")
    private Worker author;

    @AssertTrue(message = "(work reports) exactly one of work task/work step must be set")
    public boolean isExactlyOneReportTarget() {
        return (workTask != null) ^ (workStep != null);
    }
}
