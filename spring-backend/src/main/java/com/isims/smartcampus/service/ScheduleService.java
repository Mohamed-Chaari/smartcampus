package com.isims.smartcampus.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final List<ScheduleEntry> schedule = new ArrayList<>();

    public record ScheduleEntry(String day, LocalTime start, LocalTime end, String roomName) {}

    @PostConstruct
    public void init() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/rooms.csv")))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    schedule.add(new ScheduleEntry(
                            parts[0],
                            LocalTime.parse(parts[1]),
                            LocalTime.parse(parts[2]),
                            parts[3]
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRoomOccupied(String roomName, String day, LocalTime time) {
        return schedule.stream().anyMatch(e -> 
            e.day().equalsIgnoreCase(day) &&
            roomName.equalsIgnoreCase(e.roomName()) &&
            !time.isBefore(e.start()) && !time.isAfter(e.end())
        );
    }

    public List<String> getOccupiedRoomsNow(String day, LocalTime time) {
        return schedule.stream()
                .filter(e -> e.day().equalsIgnoreCase(day) && !time.isBefore(e.start()) && !time.isAfter(e.end()))
                .map(ScheduleEntry::roomName)
                .distinct()
                .collect(Collectors.toList());
    }
}
