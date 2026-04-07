package com.isims.smartcampus.repository;

import com.isims.smartcampus.entity.CampusUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampusUserRepository extends JpaRepository<CampusUser, Long> {

    Optional<CampusUser> findByUserId(String userId);
}
