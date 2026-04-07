package com.isims.smartcampus.dto;

public record RoomDto(
        Long id,
        String name,
        String building,
        Integer floor,
        Integer capacity,
        String type,
        Boolean hasHvac,
        Boolean currentlyOccupied
) {
}
