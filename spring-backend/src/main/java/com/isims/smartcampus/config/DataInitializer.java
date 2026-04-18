package com.isims.smartcampus.config;

import com.isims.smartcampus.entity.CampusUser;
import com.isims.smartcampus.entity.Room;
import com.isims.smartcampus.entity.enums.UserRole;
import com.isims.smartcampus.repository.CampusUserRepository;
import com.isims.smartcampus.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoomRepository roomRepository,
                               CampusUserRepository campusUserRepository) {
        return args -> {
            if (roomRepository.count() == 0) {
                // Load rooms from CSV to ensure they match the schedule
                Set<String> processedRooms = new HashSet<>();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/rooms.csv")))) {
                    String line;
                    br.readLine(); // skip header
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 6) {
                            String name = parts[3];
                            int capacity = Integer.parseInt(parts[5]);
                            
                            if (!processedRooms.contains(name)) {
                                Room.RoomType type = name.toLowerCase().contains("amphi") ? 
                                        Room.RoomType.AMPHITHEATER : Room.RoomType.CLASSROOM;
                                
                                roomRepository.save(createRoom(name, "ISIMS Campus", 0, capacity, type, true, false));
                                processedRooms.add(name);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Fallback if CSV fails
                    roomRepository.save(createRoom("Amphi A", "Main Building", 0, 300, Room.RoomType.AMPHITHEATER, true, false));
                }
            }

            if (campusUserRepository.count() == 0) {
                campusUserRepository.save(createUser("STU-001", "Alice Dupont", UserRole.STUDENT, "1234"));
                campusUserRepository.save(createUser("STU-002", "Bob Martin", UserRole.STUDENT, "1234"));
                campusUserRepository.save(createUser("PROF-001", "Dr. Karim Ben Ali", UserRole.PROFESSOR, "1234"));
                campusUserRepository.save(createUser("PROF-002", "Dr. Sonia Khelifa", UserRole.PROFESSOR, "1234"));
                campusUserRepository.save(createUser("STAFF-001", "Maintenance Team", UserRole.MAINTENANCE, "1234"));
                campusUserRepository.save(createUser("ADMIN-001", "Campus Admin", UserRole.ADMIN, "1234"));
            }
        };
    }

    private Room createRoom(String name, String building, int floor, int capacity,
                             Room.RoomType type, boolean hasHvac, boolean occupied) {
        Room room = new Room();
        room.setName(name);
        room.setBuilding(building);
        room.setFloor(floor);
        room.setCapacity(capacity);
        room.setType(type);
        room.setHasHvac(hasHvac);
        room.setCurrentlyOccupied(occupied);
        return room;
    }

    private CampusUser createUser(String userId, String name, UserRole role, String password) {
        CampusUser user = new CampusUser();
        user.setUserId(userId);
        user.setPassword(password);
        user.setName(name);
        user.setRole(role);
        user.setTotalEcoPoints(0);
        return user;
    }
}
