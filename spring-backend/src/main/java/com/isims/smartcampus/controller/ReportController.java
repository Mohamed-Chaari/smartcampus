package com.isims.smartcampus.controller;

import com.isims.smartcampus.dto.EcoIssueDto;
import com.isims.smartcampus.dto.ReportResponseDto;
import com.isims.smartcampus.dto.UserPointsDto;
import com.isims.smartcampus.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            @RequestParam("studentId") String studentId,
            @RequestParam("location") String location,
            @RequestParam("priority") String priority,
            @RequestParam("equipmentType") String equipmentType) {

        try {
            ReportResponseDto response = reportService.saveReport(
                    image, description, studentId, location, priority, equipmentType);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(new ReportResponseDto(0L, "SPAM_REJECTED", 0, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EcoIssueDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/user/{userId}/points")
    public ResponseEntity<UserPointsDto> getUserPoints(@PathVariable String userId) {
        UserPointsDto points = reportService.getUserPoints(userId);
        return ResponseEntity.ok(points);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<java.util.List<UserPointsDto>> getLeaderboard() {
        return ResponseEntity.ok(reportService.getStudentLeaderboard());
    }

    @GetMapping("/stats")
    public ResponseEntity<com.isims.smartcampus.dto.StatsDto> getCampusStats() {
        return ResponseEntity.ok(reportService.getCampusStats());
    }

    @PatchMapping("/{issueId}/status")
    public ResponseEntity<EcoIssueDto> updateIssueStatus(@PathVariable Long issueId, @RequestParam("status") String status) {
        try {
            com.isims.smartcampus.entity.enums.IssueStatus issueStatus = com.isims.smartcampus.entity.enums.IssueStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(reportService.updateIssueStatus(issueId, issueStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
