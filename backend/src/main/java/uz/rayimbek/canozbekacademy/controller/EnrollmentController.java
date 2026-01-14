package uz.rayimbek.canozbekacademy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.rayimbek.canozbekacademy.dto.response.EnrollmentResponse;
import uz.rayimbek.canozbekacademy.security.UserPrincipal;
import uz.rayimbek.canozbekacademy.service.EnrollmentService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Enroll in course
     * POST /api/v1/enrollments/course/{courseId}
     */
    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Enroll user {} in course {}", currentUser.getEmail(), courseId);
        EnrollmentResponse enrollment = enrollmentService.enrollInCourse(courseId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    /**
     * Get my enrollments
     * GET /api/v1/enrollments/my-courses
     */
    @GetMapping("/my-courses")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get enrollments for user: {}", currentUser.getEmail());
        List<EnrollmentResponse> enrollments = enrollmentService.getUserEnrollments(currentUser.getId());
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Check if enrolled
     * GET /api/v1/enrollments/check/{courseId}
     */
    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkEnrollment(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        boolean isEnrolled = enrollmentService.isEnrolled(courseId, currentUser.getId());
        return ResponseEntity.ok(Map.of("isEnrolled", isEnrolled));
    }
}
