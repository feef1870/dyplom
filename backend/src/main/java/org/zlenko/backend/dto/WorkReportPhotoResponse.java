package org.zlenko.backend.dto;

import org.zlenko.backend.entities.WorkReportPhoto;

public record WorkReportPhotoResponse(
        Long id,
        String originalFilename,
        String contentType
) {
    public static WorkReportPhotoResponse from(WorkReportPhoto photo) {
        return new WorkReportPhotoResponse(
                photo.getId(),
                photo.getOriginalFilename(),
                photo.getContentType()
        );
    }
}
