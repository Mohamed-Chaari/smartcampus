package com.isims.smartcampus.repository;

import com.isims.smartcampus.entity.CampusUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CampusUserRepository extends JpaRepository<CampusUser, Long> {

    Optional<CampusUser> findByUserId(String userId);

    java.util.List<CampusUser> findByRoleOrderByTotalEcoPointsDesc(com.isims.smartcampus.entity.enums.UserRole role);

    @Modifying
    @Transactional
    @Query("UPDATE CampusUser u SET u.totalEcoPoints = u.totalEcoPoints + :points WHERE u.userId = :userId")
    int incrementEcoPoints(@Param("userId") String userId, @Param("points") int points);
}
