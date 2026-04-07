package com.isims.smartcampus.service;

import com.isims.smartcampus.dto.EcoIssueDto;
import com.isims.smartcampus.dto.GeminiAnalysisResult;
import com.isims.smartcampus.dto.ReportResponseDto;
import com.isims.smartcampus.dto.UserPointsDto;
import com.isims.smartcampus.entity.CampusUser;
import com.isims.smartcampus.entity.EcoIssue;
import com.isims.smartcampus.repository.CampusUserRepository;
import com.isims.smartcampus.repository.EcoIssueRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final EcoIssueRepository ecoIssueRepository;
    private final CampusUserRepository campusUserRepository;
    private final GeminiService geminiService;
    private final String uploadDir;

    public ReportService(
            EcoIssueRepository ecoIssueRepository,
            CampusUserRepository campusUserRepository,
            GeminiService geminiService,
            @Value("${app.upload.dir}") String uploadDir) {
        this.ecoIssueRepository = ecoIssueRepository;
        this.campusUserRepository = campusUserRepository;
        this.geminiService = geminiService;
        this.uploadDir = uploadDir;
    }

    @Transactional
    public ReportResponseDto saveReport(MultipartFile image, String description, String studentId) {
        try {
            // 1. Save Image Locally
            String filename = saveImageLocally(image);
            String imageUrl = "/" + uploadDir + "/" + filename;

            // 2. Convert to Base64 for Gemini
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 3. Call Gemini API
            GeminiAnalysisResult analysis = geminiService.analyzeImage(base64Image, description);

            // 4. Save to Database
            EcoIssue issue = new EcoIssue();
            issue.setDescription(description);
            issue.setStudentId(studentId);
            issue.setImageUrl(imageUrl);
            issue.setCategory(analysis.category());
            issue.setEcoPoints(analysis.ecoPoints());

            EcoIssue savedIssue = ecoIssueRepository.save(issue);

            // 5. Award eco-points to the reporting user
            awardEcoPoints(studentId, analysis.ecoPoints());

            // 6. Return Response
            return new ReportResponseDto(
                    savedIssue.getId(),
                    savedIssue.getCategory(),
                    savedIssue.getEcoPoints(),
                    analysis.reasoning()
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to save report: " + e.getMessage(), e);
        }
    }

    public List<EcoIssueDto> getAllReports() {
        return ecoIssueRepository.findAll().stream()
                .map(i -> new EcoIssueDto(
                        i.getId(),
                        i.getDescription(),
                        i.getStudentId(),
                        i.getImageUrl(),
                        i.getCategory(),
                        i.getEcoPoints(),
                        i.getReportedAt()))
                .collect(Collectors.toList());
    }

    public UserPointsDto getUserPoints(String userId) {
        CampusUser user = campusUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return new UserPointsDto(user.getUserId(), user.getName(), user.getTotalEcoPoints());
    }

    private void awardEcoPoints(String studentId, int points) {
        campusUserRepository.incrementEcoPoints(studentId, points);
    }

    private String saveImageLocally(MultipartFile image) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";

        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFilename);
        image.transferTo(filePath.toFile());

        return newFilename;
    }
}
