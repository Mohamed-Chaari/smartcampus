package com.isims.smartcampus.controller;

import com.isims.smartcampus.dto.ReportResponseDto;
import com.isims.smartcampus.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ReportResponseDto> submitReport(
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description,
            @RequestParam("studentId") String studentId) {

        ReportResponseDto response = reportService.saveReport(image, description, studentId);
        return ResponseEntity.ok(response);
    }
}
