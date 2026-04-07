package com.isims.smartcampus.controller;

import com.isims.smartcampus.dto.RelocationRequestDto;
import com.isims.smartcampus.dto.RelocationResponseDto;
import com.isims.smartcampus.dto.RoomDto;
import com.isims.smartcampus.service.RelocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relocation")
@CrossOrigin(origins = "*")
public class RelocationController {

    private final RelocationService relocationService;

    public RelocationController(RelocationService relocationService) {
        this.relocationService = relocationService;
    }

    @PostMapping("/request")
    public ResponseEntity<RelocationResponseDto> requestRelocation(
            @RequestBody RelocationRequestDto dto) {
        RelocationResponseDto response = relocationService.requestRelocation(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> getRooms() {
        return ResponseEntity.ok(relocationService.getAllRooms());
    }
}
