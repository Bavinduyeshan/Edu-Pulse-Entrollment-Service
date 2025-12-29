package com.Edupulse.EntrollmentService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailsResponse {
    private UserResponse student;
    private List<EnrolledClassDto> enrolledClasses;
    private List<AttendanceRecordDto> attendanceRecords;
    private List<QuizResultDto> quizResults;
    private Integer totalLectures;
    private Integer attendedLectures;
    private Double averageQuizScore;
}