package com.isims.smartcampus.config;

import com.isims.smartcampus.entity.CampusUser;
import com.isims.smartcampus.entity.Room;
import com.isims.smartcampus.repository.CampusUserRepository;
import com.isims.smartcampus.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoomRepository roomRepository,
                               CampusUserRepository campusUserRepository) {
        return args -> {
            if (roomRepository.count() == 0) {
                roomRepository.save(createRoom("Amphi A", "Main Building", 0, 300, Room.RoomType.AMPHITHEATER, true, false));
                roomRepository.save(createRoom("Amphi B", "Main Building", 0, 250, Room.RoomType.AMPHITHEATER, true, false));
                roomRepository.save(createRoom("Room 101", "Science Block", 1, 60, Room.RoomType.CLASSROOM, true, false));
                roomRepository.save(createRoom("Room 102", "Science Block", 1, 60, Room.RoomType.CLASSROOM, true, false));
                roomRepository.save(createRoom("Room 201", "Science Block", 2, 40, Room.RoomType.CLASSROOM, false, false));
                roomRepository.save(createRoom("Lab 1", "Engineering Block", 1, 30, Room.RoomType.LAB, true, false));
                roomRepository.save(createRoom("Lab 2", "Engineering Block", 2, 30, Room.RoomType.LAB, true, false));
                roomRepository.save(createRoom("Room 301", "Humanities Block", 3, 50, Room.RoomType.CLASSROOM, true, false));
            }

            if (campusUserRepository.count() == 0) {
                campusUserRepository.save(createUser("STU-001", "Alice Dupont", CampusUser.UserRole.STUDENT));
                campusUserRepository.save(createUser("STU-002", "Bob Martin", CampusUser.UserRole.STUDENT));
                campusUserRepository.save(createUser("PROF-001", "Dr. Karim Ben Ali", CampusUser.UserRole.PROFESSOR));
                campusUserRepository.save(createUser("PROF-002", "Dr. Sonia Khelifa", CampusUser.UserRole.PROFESSOR));
                campusUserRepository.save(createUser("STAFF-001", "Maintenance Team", CampusUser.UserRole.STAFF));
                campusUserRepository.save(createUser("ADMIN-001", "Campus Admin", CampusUser.UserRole.ADMIN));
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

    private CampusUser createUser(String userId, String name, CampusUser.UserRole role) {
        CampusUser user = new CampusUser();
        user.setUserId(userId);
        user.setName(name);
        user.setRole(role);
        user.setTotalEcoPoints(0);
        return user;
    }
}
