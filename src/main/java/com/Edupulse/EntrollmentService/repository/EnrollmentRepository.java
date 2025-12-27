package com.Edupulse.EntrollmentService.repository;

import com.Edupulse.EntrollmentService.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Find active enrollment for a specific student and class
     */
    Optional<Enrollment> findByStudentIdAndClassIdAndActiveTrue(Long studentId, Long classId);

    /**
     * Find all active enrollments for a student (for "My Classes")
     */
    List<Enrollment> findByStudentIdAndActiveTrue(Long studentId);

    /**
     * Find all active enrollments for a class (for lecturer to see enrolled students)
     */
    @Query("SELECT e FROM Enrollment e WHERE e.classId = :classId AND e.active = true")
    List<Enrollment> findActiveEnrollmentsByClassId(@Param("classId") Long classId);

    /**
     * Quick check if student is currently enrolled in a class
     */
    boolean existsByStudentIdAndClassIdAndActiveTrue(Long studentId, Long classId);

    /**
     * Count total active enrollments per class
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.classId = :classId AND e.active = true")
    long countActiveEnrollmentsByClassId(@Param("classId") Long classId);

    /**
     * Find all historical enrollments for a student (active + inactive) for audit
     */
    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByClassIdAndActiveTrue(Long classId);

    // 🔢 NEW: count students in a class
    long countByClassIdAndActiveTrue(Long classId);

    // 🔢 NEW: get all enrollments for multiple classes
    List<Enrollment> findByClassIdInAndActiveTrue(List<Long> classIds);
    List<Enrollment> findByStudentIdAndClassIdInAndActiveTrue(Long studentId, List<Long> classIds);

}