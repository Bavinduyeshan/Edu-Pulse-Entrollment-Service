package com.Edupulse.EntrollmentService.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// EnrollmentResponse.java
@Data
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private Long classId;
    private String className;
    private LocalDateTime enrolledAt;
    private boolean active;
}