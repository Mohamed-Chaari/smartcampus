package com.isims.smartcampus.dto;

import com.isims.smartcampus.entity.enums.IssuePriority;
import com.isims.smartcampus.entity.enums.IssueStatus;
import java.time.LocalDateTime;

public record EcoIssueDto(
        Long id,
        String description,
        String studentId,
        String imageUrl,
        String category,
        Integer ecoPoints,
        LocalDateTime reportedAt,
        IssuePriority priority,
        IssueStatus status,
        String location,
        String equipmentType
) {
}
