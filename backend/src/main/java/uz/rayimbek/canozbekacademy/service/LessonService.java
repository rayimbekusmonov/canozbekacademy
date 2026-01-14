package uz.rayimbek.canozbekacademy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rayimbek.canozbekacademy.dto.request.UpdateProgressRequest;
import uz.rayimbek.canozbekacademy.dto.response.LessonDetailResponse;
import uz.rayimbek.canozbekacademy.dto.response.LessonProgressResponse;
import uz.rayimbek.canozbekacademy.entity.*;
import uz.rayimbek.canozbekacademy.exception.BadRequestException;
import uz.rayimbek.canozbekacademy.exception.ResourceNotFoundException;
import uz.rayimbek.canozbekacademy.exception.UnauthorizedException;
import uz.rayimbek.canozbekacademy.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    /**
     * Get lesson by ID (with access check)
     */
    @Transactional(readOnly = true)
    public LessonDetailResponse getLessonById(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if lesson is locked
        if (lesson.isLocked(user)) {
            throw new UnauthorizedException("This lesson is locked. Complete previous lessons first.");
        }

        return mapToLessonDetailResponse(lesson, user);
    }

    /**
     * Get lesson progress
     */
    @Transactional(readOnly = true)
    public LessonProgressResponse getLessonProgress(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        LessonProgress progress = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(null);

        return mapToProgressResponse(lesson, progress);
    }

    /**
     * Update video progress
     */
    @Transactional
    public LessonProgressResponse updateProgress(
            Long lessonId,
            Long userId,
            UpdateProgressRequest request
    ) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check enrollment
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(
                userId, lesson.getSection().getCourse().getId());

        if (!isEnrolled && !lesson.getIsPreview()) {
            throw new UnauthorizedException("You must be enrolled to access this lesson");
        }

        // Get or create progress
        LessonProgress progress = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(LessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .isCompleted(false)
                        .videoProgress(0)
                        .build());

        // Update progress
        progress.setVideoProgress(request.getVideoProgress());

        // Auto-complete if video watched 90%+
        if (lesson.getVideoDuration() != null &&
                request.getVideoProgress() >= (lesson.getVideoDuration() * 0.9)) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }

        progress = progressRepository.save(progress);
        log.info("Progress updated for lesson {} by user {}: {}s",
                lessonId, userId, request.getVideoProgress());

        // Update enrollment progress
        updateEnrollmentProgress(userId, lesson.getSection().getCourse().getId());

        return mapToProgressResponse(lesson, progress);
    }

    /**
     * Mark lesson as complete (manual)
     */
    @Transactional
    public LessonProgressResponse completeLesson(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if all assignments are completed
        List<Assignment> assignments = assignmentRepository.findByLessonId(lessonId);
        if (!assignments.isEmpty()) {
            // TODO: Check if all assignments are submitted and graded
            // For now, just log
            log.info("Lesson {} has {} assignments", lessonId, assignments.size());
        }

        // Get or create progress
        LessonProgress progress = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(LessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .videoProgress(0)
                        .build());

        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progress = progressRepository.save(progress);

        log.info("Lesson {} marked as complete by user {}", lessonId, userId);

        // Update enrollment progress
        updateEnrollmentProgress(userId, lesson.getSection().getCourse().getId());

        return mapToProgressResponse(lesson, progress);
    }

    /**
     * Get all lessons for a course section
     */
    @Transactional(readOnly = true)
    public List<LessonDetailResponse> getLessonsBySection(Long sectionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Lesson> lessons = lessonRepository.findBySectionIdOrderByOrderIndexAsc(sectionId);

        return lessons.stream()
                .map(lesson -> mapToLessonDetailResponse(lesson, user))
                .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

    private void updateEnrollmentProgress(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElse(null);

        if (enrollment == null) return;

        // Calculate total lessons and completed lessons
        Course course = enrollment.getCourse();
        long totalLessons = course.getSections().stream()
                .flatMap(section -> section.getLessons().stream())
                .count();

        long completedLessons = progressRepository.countByUserIdAndIsCompletedTrue(userId);

        if (totalLessons > 0) {
            double percentage = (completedLessons * 100.0) / totalLessons;
            enrollment.setProgressPercentage(java.math.BigDecimal.valueOf(percentage));

            // Check if completed
            if (completedLessons >= totalLessons) {
                enrollment.setCompletedAt(LocalDateTime.now());
            }

            enrollmentRepository.save(enrollment);
        }
    }

    private LessonDetailResponse mapToLessonDetailResponse(Lesson lesson, User user) {
        LessonProgress progress = progressRepository
                .findByUserIdAndLessonId(user.getId(), lesson.getId())
                .orElse(null);

        return LessonDetailResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .videoUrl(lesson.getVideoUrl())
                .videoDuration(lesson.getVideoDuration())
                .formattedDuration(lesson.getFormattedDuration())
                .orderIndex(lesson.getOrderIndex())
                .isPreview(lesson.getIsPreview())
                .isLocked(lesson.isLocked(user))
                .isCompleted(progress != null && progress.getIsCompleted())
                .videoProgress(progress != null ? progress.getVideoProgress() : 0)
                .resources(lesson.getResources())
                .hasAssignments(!lesson.getAssignments().isEmpty())
                .assignmentsCount(lesson.getAssignments().size())
                .build();
    }

    private LessonProgressResponse mapToProgressResponse(Lesson lesson, LessonProgress progress) {
        return LessonProgressResponse.builder()
                .lessonId(lesson.getId())
                .isCompleted(progress != null && progress.getIsCompleted())
                .videoProgress(progress != null ? progress.getVideoProgress() : 0)
                .completedAt(progress != null ? progress.getCompletedAt() : null)
                .lastWatchedAt(progress != null ? progress.getLastWatchedAt() : null)
                .build();
    }
}