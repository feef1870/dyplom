package org.zlenko.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Table(name = "work_report_photos")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class WorkReportPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_report_id")
    @NotNull(message = "(work report photos) work report id is null")
    private WorkReport workReport;

    @Column(name = "storage_key", nullable = false)
    @NotBlank(message = "(work report photos) storage key is null")
    private String storageKey;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "(work report photos) created at is null")
    @CreationTimestamp
    private Instant createdAt;
}
