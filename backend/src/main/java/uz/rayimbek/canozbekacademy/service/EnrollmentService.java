package uz.rayimbek.canozbekacademy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rayimbek.canozbekacademy.dto.response.EnrollmentResponse;
import uz.rayimbek.canozbekacademy.entity.Course;
import uz.rayimbek.canozbekacademy.entity.Enrollment;
import uz.rayimbek.canozbekacademy.entity.User;
import uz.rayimbek.canozbekacademy.exception.BadRequestException;
import uz.rayimbek.canozbekacademy.exception.ResourceNotFoundException;
import uz.rayimbek.canozbekacademy.repository.CourseRepository;
import uz.rayimbek.canozbekacademy.repository.EnrollmentRepository;
import uz.rayimbek.canozbekacademy.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Enroll user in a course
     */
    @Transactional
    public EnrollmentResponse enrollInCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new BadRequestException("You are already enrolled in this course");
        }

        // Check if course is published
        if (!course.getIsPublished()) {
            throw new BadRequestException("This course is not available for enrollment");
        }

        // Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .progressPercentage(BigDecimal.ZERO)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        // Update course total students
        course.setTotalStudents(course.getTotalStudents() + 1);
        courseRepository.save(course);

        // Send enrollment confirmation email
        emailService.sendEnrollmentConfirmationEmail(user, course.getTitle());

        log.info("User {} enrolled in course {}", user.getEmail(), course.getTitle());

        return mapToEnrollmentResponse(enrollment);
    }

    /**
     * Get user enrollments
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getUserEnrollments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get enrollment by course and user
     */
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollment(Long courseId, Long userId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        return mapToEnrollmentResponse(enrollment);
    }

    /**
     * Check if user is enrolled
     */
    @Transactional(readOnly = true)
    public boolean isEnrolled(Long courseId, Long userId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Update enrollment progress
     */
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, BigDecimal progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        enrollment.setProgressPercentage(progress);

        // Check if completed
        if (progress.compareTo(BigDecimal.valueOf(100)) >= 0) {
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
//             TODO: Generate certificate
        }

        enrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment progress updated: {}%", progress);

        return mapToEnrollmentResponse(enrollment);
    }

    // ==================== Helper Methods ====================

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseSlug(enrollment.getCourse().getSlug())
                .courseThumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .progressPercentage(enrollment.getProgressPercentage())
                .certificateUrl(enrollment.getCertificateUrl())
                .build();
    }
}