package com.Edupulse.EntrollmentService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {
    private Long quizId;
    private String title;
    private Long studentId;
    private String studentName;
    private Double score;
    private String passed; // "PASS" or "FAIL"
    private Integer timeTaken;
    private LocalDateTime submittedAt;
}