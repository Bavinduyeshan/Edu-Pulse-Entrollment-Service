package com.Edupulse.EntrollmentService.service;

import com.Edupulse.EntrollmentService.model.dto.QuizResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "quiz-service", url = "${QUIZ_SERVICE_URL:http://localhost:8083}")
public interface QuizServiceClient {

    @GetMapping("/api/quizzes/student/{studentId}/results")
    List<QuizResultDto> getQuizResultsForStudent(
            @PathVariable("studentId") Long studentId,
            @RequestHeader("X-User-Id") Long lecturerId
    );
}