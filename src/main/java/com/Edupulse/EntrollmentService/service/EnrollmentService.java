package com.Edupulse.EntrollmentService.service;

import com.Edupulse.EntrollmentService.model.Enrollment;
import com.Edupulse.EntrollmentService.model.dto.ClassResponse;
import com.Edupulse.EntrollmentService.model.dto.EnrollmentRequest;
import com.Edupulse.EntrollmentService.model.dto.EnrollmentResponse;
import com.Edupulse.EntrollmentService.model.dto.UserResponse;
import com.Edupulse.EntrollmentService.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserServiceClient userServiceClient;    // Feign to UserService
    private final ClassServiceClient classServiceClient;  // Feign to ClassService

    /**
     * Enroll a student in a class
     */
    @Transactional
    public EnrollmentResponse enroll(Long studentId, EnrollmentRequest request) {
        Long classId = request.getClassId();

        // 1. Validate student exists and is a STUDENT
        UserResponse student = userServiceClient.validateStudent(studentId);
        if (student == null || !"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new IllegalArgumentException("Invalid or unauthorized student");
        }

        // 2. Check if already enrolled (active)
        if (enrollmentRepository.findByStudentIdAndClassIdAndActiveTrue(studentId, classId).isPresent()) {
            throw new IllegalStateException("Student is already enrolled in this class");
        }

        // 3. Validate class exists (via ClassService)
        ClassResponse classInfo = classServiceClient.getClassById(classId);
        if (classInfo == null) {
            throw new IllegalArgumentException("Class not found");
        }

        // 4. Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .classId(classId)
                .enrolledAt(LocalDateTime.now())
                .active(true)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        // 5. Return response
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(studentId)
                .classId(classId)
                .className(classInfo.getName())
                .enrolledAt(enrollment.getEnrolledAt())
                .active(true)
                .build();
    }

    /**
     * Unenroll (soft delete) a student from a class
     */
    @Transactional
    public void unenroll(Long studentId, Long classId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndClassIdAndActiveTrue(studentId, classId)
                .orElseThrow(() -> new IllegalStateException("Enrollment not found or already inactive"));

        enrollment.setActive(false);
        enrollment.setUnenrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    /**
     * Get "My Classes" list for a student (returns ClassResponse from ClassService)
     */
    public List<ClassResponse> getMyClasses(Long studentId) {
        // Optional: Validate student
        userServiceClient.validateStudent(studentId);

        List<Enrollment> activeEnrollments = enrollmentRepository.findByStudentIdAndActiveTrue(studentId);

        return activeEnrollments.stream()
                .map(enrollment -> classServiceClient.getClassById(enrollment.getClassId()))
                .collect(Collectors.toList());
    }
}