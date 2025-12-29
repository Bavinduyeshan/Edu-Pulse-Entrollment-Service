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
public class LectureResponse {
    private Long id;
    private Long classId;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String videoLink;
    private String pdfUrl;
    private LocalDateTime createdAt;
}