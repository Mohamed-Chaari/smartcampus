package com.isims.smartcampus.service;

import com.isims.smartcampus.dto.RelocationRequestDto;
import com.isims.smartcampus.dto.RelocationResponseDto;
import com.isims.smartcampus.dto.RoomDto;
import com.isims.smartcampus.entity.RelocationRequest;
import com.isims.smartcampus.entity.Room;
import com.isims.smartcampus.repository.RelocationRequestRepository;
import com.isims.smartcampus.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelocationService {

    private static final int OCCUPANCY_THRESHOLD_PERCENT = 40;

    private final RoomRepository roomRepository;
    private final RelocationRequestRepository relocationRequestRepository;

    public RelocationService(RoomRepository roomRepository,
                              RelocationRequestRepository relocationRequestRepository) {
        this.roomRepository = roomRepository;
        this.relocationRequestRepository = relocationRequestRepository;
    }

    public RelocationResponseDto requestRelocation(RelocationRequestDto dto) {
        Room originalRoom = roomRepository.findById(dto.originalRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + dto.originalRoomId()));

        int attendance = dto.reportedAttendance();
        int occupancyPercent = (int) ((double) attendance / originalRoom.getCapacity() * 100);
        boolean hvacShutdown = occupancyPercent < OCCUPANCY_THRESHOLD_PERCENT;

        List<Room> candidates = roomRepository
                .findByCurrentlyOccupiedFalseAndCapacityGreaterThanEqualOrderByCapacityAsc(attendance);

        Room suggestedRoom = candidates.stream()
                .filter(r -> !r.getId().equals(originalRoom.getId()))
                .findFirst()
                .orElse(null);

        RelocationRequest request = new RelocationRequest();
        request.setProfessorId(dto.professorId());
        request.setOriginalRoom(originalRoom);
        request.setReportedAttendance(attendance);
        request.setSuggestedRoom(suggestedRoom);
        request.setStatus(suggestedRoom != null
                ? RelocationRequest.RelocationStatus.APPROVED
                : RelocationRequest.RelocationStatus.PENDING);
        relocationRequestRepository.save(request);

        if (suggestedRoom != null) {
            suggestedRoom.setCurrentlyOccupied(true);
            roomRepository.save(suggestedRoom);
        }

        String message = suggestedRoom != null
                ? "Relocation approved. Please move to " + suggestedRoom.getName()
                  + (hvacShutdown ? ". HVAC and lighting shutdown recommended for " + originalRoom.getName() + "." : ".")
                : "No suitable vacant room found at this time. Request is pending.";

        return new RelocationResponseDto(
                request.getId(),
                originalRoom.getName(),
                attendance,
                suggestedRoom != null ? suggestedRoom.getName() : null,
                suggestedRoom != null ? suggestedRoom.getBuilding() : null,
                suggestedRoom != null ? suggestedRoom.getCapacity() : null,
                hvacShutdown,
                message
        );
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(r -> new RoomDto(
                        r.getId(),
                        r.getName(),
                        r.getBuilding(),
                        r.getFloor(),
                        r.getCapacity(),
                        r.getType().name(),
                        r.getHasHvac(),
                        r.getCurrentlyOccupied()))
                .collect(Collectors.toList());
    }
}
