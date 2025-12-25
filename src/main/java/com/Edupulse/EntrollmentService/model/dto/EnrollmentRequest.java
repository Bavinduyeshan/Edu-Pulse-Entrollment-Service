package com.Edupulse.EntrollmentService.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// EnrollmentRequest.java
@Data
public class EnrollmentRequest {
    @NotNull
    private Long classId;
}