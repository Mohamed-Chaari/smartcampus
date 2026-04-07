package com.isims.smartcampus.dto;

public record RelocationResponseDto(
        Long requestId,
        String originalRoomName,
        Integer reportedAttendance,
        String suggestedRoomName,
        String suggestedRoomBuilding,
        Integer suggestedRoomCapacity,
        Boolean hvacShutdownRecommended,
        String message
) {
}
