package com.isims.smartcampus.dto;

public record StatsDto(
        long totalAnomalies,
        long pendingAnomalies,
        long totalRelocations
) {
}
