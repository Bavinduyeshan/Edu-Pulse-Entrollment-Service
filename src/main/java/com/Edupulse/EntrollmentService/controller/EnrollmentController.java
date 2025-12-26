package com.Edupulse.EntrollmentService.controller;

import com.Edupulse.EntrollmentService.model.dto.ClassResponse;
import com.Edupulse.EntrollmentService.model.dto.EnrollmentRequest;
import com.Edupulse.EntrollmentService.model.dto.EnrollmentResponse;
import com.Edupulse.EntrollmentService.model.dto.UserResponse;
import com.Edupulse.EntrollmentService.service.ClassServiceClient;
import com.Edupulse.EntrollmentService.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})

public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final ClassServiceClient classServiceClient;  // Feign to ClassService


    /**
     * Enroll current student in a class
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody EnrollmentRequest request,
            @RequestHeader("X-User-Id") Long studentId) {

        EnrollmentResponse response = enrollmentService.enroll(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Unenroll current student from a class
     */
    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> unenroll(
            @PathVariable Long classId,
            @RequestHeader("X-User-Id") Long studentId) {

        enrollmentService.unenroll(studentId, classId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get "My Classes" list (returns ClassResponse from ClassService)
     */
    @GetMapping("/my-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ClassResponse>> getMyClasses(
            @RequestHeader("X-User-Id") Long studentId) {

        List<ClassResponse> myClasses = enrollmentService.getMyClasses(studentId);
        return ResponseEntity.ok(myClasses);
    }

    // In EnrollmentController.java (port 8082)
    @GetMapping("/class/{classId}/students")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<UserResponse>> getEnrolledStudentsForClass(
            @PathVariable Long classId,
            @RequestHeader("X-User-Id") Long lecturerId) {

        // Security: Verify lecturer owns/teaches this class (via ClassService Feign)
        ClassResponse classInfo = classServiceClient.getClassById(classId);
        if (classInfo == null || !classInfo.getLecturerId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UserResponse> enrolledStudents = enrollmentService.getEnrolledStudentsForClass(classId);
        return ResponseEntity.ok(enrolledStudents);
    }
}