package com.isims.smartcampus.repository;

import com.isims.smartcampus.entity.RelocationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelocationRequestRepository extends JpaRepository<RelocationRequest, Long> {

    List<RelocationRequest> findByProfessorIdOrderByRequestedAtDesc(String professorId);
}
