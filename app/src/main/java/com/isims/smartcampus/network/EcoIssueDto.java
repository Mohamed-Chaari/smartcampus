package com.isims.smartcampus.network;

public class EcoIssueDto {
    private Long id;
    private String description;
    private String studentId;
    private String imageUrl;
    private String category;
    private Integer ecoPoints;
    private String reportedAt;
    private String priority;
    private String status;
    private String location;
    private String equipmentType;

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public String getStudentId() { return studentId; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public Integer getEcoPoints() { return ecoPoints; }
    public String getReportedAt() { return reportedAt; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
    public String getEquipmentType() { return equipmentType; }
}
