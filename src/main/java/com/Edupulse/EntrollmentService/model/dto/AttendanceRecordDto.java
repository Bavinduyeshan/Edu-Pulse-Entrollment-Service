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
public class AttendanceRecordDto {
    private Long lectureId;
    private String lectureTitle;
    private String status; // PRESENT, LATE, ABSENT
    private LocalDateTime markedAt;
}