package com.Edupulse.EntrollmentService.service;

import com.Edupulse.EntrollmentService.model.Enrollment;
import com.Edupulse.EntrollmentService.model.dto.*;
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
    private final QuizServiceClient quizServiceClient;

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


    public List<UserResponse> getEnrolledStudentsForClass(Long classId) {
        List<Enrollment> enrollments = enrollmentRepository.findByClassIdAndActiveTrue(classId);


        return enrollments.stream()
                .map(enrollment -> userServiceClient.validateStudent(enrollment.getStudentId()))
                .filter(user -> user != null && "STUDENT".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    public long getStudentCountForClass(Long classId) {
        return enrollmentRepository.countByClassIdAndActiveTrue(classId);
    }

    public long getTotalStudentCountForLecturer(Long lecturerId) {

        // 1. Get all lecturer classes
        List<ClassResponse> classes =
                classServiceClient.getClassesByLecturer(lecturerId, lecturerId);

        List<Long> classIds = classes.stream()
                .map(ClassResponse::getId)
                .toList();

        // 2. Count unique students
        return enrollmentRepository.findByClassIdInAndActiveTrue(classIds)
                .stream()
                .map(Enrollment::getStudentId)
                .distinct()
                .count();
    }


    public List<UserResponse> getAllStudentsForLecturer(Long lecturerId) {

        List<ClassResponse> classes =
                classServiceClient.getClassesByLecturer(lecturerId, lecturerId);

        List<Long> classIds = classes.stream()
                .map(ClassResponse::getId)
                .toList();

        return enrollmentRepository.findByClassIdInAndActiveTrue(classIds)
                .stream()
                .map(enrollment -> userServiceClient.validateStudent(enrollment.getStudentId()))
                .distinct()
                .toList();
    }


    public StudentDetailsResponse getStudentDetailsForLecturer(Long studentId, Long lecturerId) {
        // 1. Get student info
        UserResponse student = userServiceClient.validateStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found");
        }

        // 2. Get lecturer's classes
        List<ClassResponse> lecturerClasses = classServiceClient.getClassesByLecturer(lecturerId, lecturerId);
        List<Long> lecturerClassIds = lecturerClasses.stream()
                .map(ClassResponse::getId)
                .toList();

        // 3. Get student's enrollments in lecturer's classes only
        List<Enrollment> studentEnrollments = enrollmentRepository
                .findByStudentIdAndClassIdInAndActiveTrue(studentId, lecturerClassIds);

        List<EnrolledClassDto> enrolledClasses = studentEnrollments.stream()
                .map(enrollment -> {
                    ClassResponse classInfo = lecturerClasses.stream()
                            .filter(c -> c.getId().equals(enrollment.getClassId()))
                            .findFirst()
                            .orElse(null);

                    return EnrolledClassDto.builder()
                            .classId(enrollment.getClassId())
                            .className(classInfo != null ? classInfo.getName() : "Unknown")
                            .enrolledAt(enrollment.getEnrolledAt())
                            .build();
                })
                .toList();

        // 4. Get attendance records from ClassService
        List<AttendanceResponse> allAttendanceRecords =
                classServiceClient.getAttendanceForStudent(studentId, lecturerId);

        // Convert AttendanceResponse to AttendanceRecordDto
        List<AttendanceRecordDto> attendanceRecords = allAttendanceRecords.stream()
                .map(attendance -> AttendanceRecordDto.builder()
                        .lectureId(attendance.getLectureId())
                        .lectureTitle(attendance.getLectureTitle())
                        .status(attendance.getStatus())
                        .markedAt(attendance.getCheckInTime())
                        .build())
                .toList();

        // 5. Get quiz results from QuizService
        List<QuizResultDto> quizResults;
        try {
            quizResults = quizServiceClient.getQuizResultsForStudent(studentId, lecturerId);
        } catch (Exception e) {
            System.out.println("Warning: Could not fetch quiz results - " + e.getMessage());
            quizResults = List.of();
        }

        // 6. Calculate statistics
        List<Long> enrolledClassIds = studentEnrollments.stream()
                .map(Enrollment::getClassId)
                .toList();

        int totalLectures = 0;
        for (Long classId : enrolledClassIds) {
            try {
                List<LectureResponse> lectures = classServiceClient.getLecturesByClass(classId);
                totalLectures += lectures.size();
            } catch (Exception e) {
                System.out.println("Warning: Could not fetch lectures for class " + classId);
            }
        }

        int attendedLectures = (int) attendanceRecords.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
                .count();

        double averageQuizScore = quizResults.isEmpty() ? 0.0 :
                quizResults.stream()
                        .mapToDouble(QuizResultDto::getScore)
                        .average()
                        .orElse(0.0);

        // 7. Build response
        return StudentDetailsResponse.builder()
                .student(student)
                .enrolledClasses(enrolledClasses)
                .attendanceRecords(attendanceRecords)
                .quizResults(quizResults)
                .totalLectures(totalLectures)
                .attendedLectures(attendedLectures)
                .averageQuizScore(averageQuizScore)
                .build();
    }



    public long getTotalEnrollmentCount() {
        return enrollmentRepository.countByActiveTrue();
    }
}