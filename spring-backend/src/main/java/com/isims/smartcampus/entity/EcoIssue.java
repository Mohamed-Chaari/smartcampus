package com.isims.smartcampus.entity;

import com.isims.smartcampus.entity.enums.IssuePriority;
import com.isims.smartcampus.entity.enums.IssueStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EcoIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String studentId;
    private String imageUrl;
    private String category;
    private Integer ecoPoints;
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.PENDING;

    private String location;
    private String equipmentType;

    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
    }
}
