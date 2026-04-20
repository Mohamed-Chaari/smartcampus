package com.isims.smartcampus.service;

import com.isims.smartcampus.dto.RelocationRequestDto;
import com.isims.smartcampus.dto.RelocationResponseDto;
import com.isims.smartcampus.dto.RoomDto;
import com.isims.smartcampus.entity.RelocationRequest;
import com.isims.smartcampus.entity.Room;
import com.isims.smartcampus.repository.RelocationRequestRepository;
import com.isims.smartcampus.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class RelocationService {

    private static final int OCCUPANCY_THRESHOLD_PERCENT = 40;

    private final RoomRepository roomRepository;
    private final RelocationRequestRepository relocationRequestRepository;
    private final ScheduleService scheduleService;

    public RelocationService(RoomRepository roomRepository,
                              RelocationRequestRepository relocationRequestRepository,
                              ScheduleService scheduleService) {
        this.roomRepository = roomRepository;
        this.relocationRequestRepository = relocationRequestRepository;
        this.scheduleService = scheduleService;
    }

    @Transactional
    public RelocationResponseDto requestRelocation(RelocationRequestDto dto) {
        Room originalRoom = roomRepository.findById(dto.originalRoomId())
                .orElse(null);

        if (originalRoom == null) {
            return new RelocationResponseDto(
                    null,
                    "Unknown",
                    dto.reportedAttendance(),
                    null,
                    null,
                    null,
                    false,
                    "Error: The specified original room ID " + dto.originalRoomId() + " does not exist."
            );
        }

        int attendance = dto.reportedAttendance();
        int occupancyPercent = (int) ((double) attendance / originalRoom.getCapacity() * 100);
        boolean hvacShutdown = occupancyPercent < OCCUPANCY_THRESHOLD_PERCENT;

        // Find current day and time in English
        LocalDateTime now = LocalDateTime.now();
        String currentDay = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        LocalTime currentTime = now.toLocalTime();

        // Find the smallest room that fits attendance with a 15% comfort buffer
        int minCapacity = (int) Math.ceil(attendance * 1.15);
        
        // Get all rooms from DB and filter by schedule
        List<Room> candidates = roomRepository.findAll().stream()
                .filter(r -> r.getCapacity() >= minCapacity)
                .filter(r -> !r.getId().equals(originalRoom.getId()))
                .filter(r -> !scheduleService.isRoomOccupied(r.getName(), currentDay, currentTime))
                .sorted((r1, r2) -> r1.getCapacity().compareTo(r2.getCapacity()))
                .collect(Collectors.toList());

        Room suggestedRoom = candidates.isEmpty() ? null : candidates.get(0);

        RelocationRequest request = new RelocationRequest();
        request.setProfessorId(dto.professorId());
        request.setOriginalRoom(originalRoom);
        request.setReportedAttendance(attendance);
        request.setSuggestedRoom(suggestedRoom);
        request.setStatus(suggestedRoom != null
                ? RelocationRequest.RelocationStatus.APPROVED
                : RelocationRequest.RelocationStatus.PENDING);
        relocationRequestRepository.save(request);

        String message = suggestedRoom != null
                ? "Relocation approved based on current schedule. Please move to " + suggestedRoom.getName()
                  + (hvacShutdown ? ". HVAC and lighting shutdown recommended for " + originalRoom.getName() + "." : ".")
                : "No suitable vacant room found in the schedule at this time. Request is pending.";

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
