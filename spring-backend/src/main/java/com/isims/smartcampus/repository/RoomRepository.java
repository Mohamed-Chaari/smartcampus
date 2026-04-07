package com.isims.smartcampus.repository;

import com.isims.smartcampus.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.currentlyOccupied = false AND r.capacity >= :minCapacity ORDER BY r.capacity ASC")
    List<Room> findAvailableRoomsWithLock(@Param("minCapacity") Integer minCapacity);
}
