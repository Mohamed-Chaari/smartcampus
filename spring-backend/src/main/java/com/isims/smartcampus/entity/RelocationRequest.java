package com.isims.smartcampus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RelocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String professorId;

    @ManyToOne
    private Room originalRoom;

    private Integer reportedAttendance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelocationStatus status = RelocationStatus.PENDING;

    @ManyToOne
    private Room suggestedRoom;

    private LocalDateTime requestedAt;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }

    public enum RelocationStatus {
        PENDING, APPROVED, COMPLETED
    }
}
