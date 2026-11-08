package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.zlenko.backend.enums.ReportStatus;

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
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "comment", nullable = false)
    @NotBlank(message = "(work reports) comment is null or blank")
    private String comment;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "report_status")
    @NotNull(message = "(work reports) status is null")
    private ReportStatus status;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

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
