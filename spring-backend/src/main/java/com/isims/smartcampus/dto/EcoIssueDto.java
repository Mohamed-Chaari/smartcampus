package com.isims.smartcampus.dto;

import java.time.LocalDateTime;

public record EcoIssueDto(
        Long id,
        String description,
        String studentId,
        String imageUrl,
        String category,
        Integer ecoPoints,
        LocalDateTime reportedAt
) {
}
