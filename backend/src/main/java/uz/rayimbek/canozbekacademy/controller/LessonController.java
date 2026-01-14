package uz.rayimbek.canozbekacademy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.rayimbek.canozbekacademy.dto.request.UpdateProgressRequest;
import uz.rayimbek.canozbekacademy.dto.response.LessonDetailResponse;
import uz.rayimbek.canozbekacademy.dto.response.LessonProgressResponse;
import uz.rayimbek.canozbekacademy.dto.response.MessageResponse;
import uz.rayimbek.canozbekacademy.security.UserPrincipal;
import uz.rayimbek.canozbekacademy.service.LessonService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    /**
     * Get lesson by ID
     * GET /api/v1/lessons/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<LessonDetailResponse> getLesson(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get lesson {} for user {}", id, currentUser.getEmail());
        LessonDetailResponse lesson = lessonService.getLessonById(id, currentUser.getId());
        return ResponseEntity.ok(lesson);
    }

    /**
     * Get lesson progress
     * GET /api/v1/lessons/{id}/progress
     */
    @GetMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<LessonProgressResponse> getProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get progress for lesson {} for user {}", id, currentUser.getEmail());
        LessonProgressResponse progress = lessonService.getLessonProgress(id, currentUser.getId());
        return ResponseEntity.ok(progress);
    }

    /**
     * Update lesson progress
     * POST /api/v1/lessons/{id}/progress
     */
    @PostMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<LessonProgressResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Update progress for lesson {} by user {}: {}s",
                id, currentUser.getEmail(), request.getVideoProgress());
        LessonProgressResponse progress = lessonService.updateProgress(
                id, currentUser.getId(), request);
        return ResponseEntity.ok(progress);
    }

    /**
     * Mark lesson as complete
     * POST /api/v1/lessons/{id}/complete
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<LessonProgressResponse> completeLesson(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Mark lesson {} as complete by user {}", id, currentUser.getEmail());
        LessonProgressResponse progress = lessonService.completeLesson(id, currentUser.getId());
        return ResponseEntity.ok(progress);
    }

    /**
     * Get lessons by section
     * GET /api/v1/lessons/section/{sectionId}
     */
    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<LessonDetailResponse>> getLessonsBySection(
            @PathVariable Long sectionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get lessons for section {} for user {}", sectionId, currentUser.getEmail());
        List<LessonDetailResponse> lessons = lessonService.getLessonsBySection(
                sectionId, currentUser.getId());
        return ResponseEntity.ok(lessons);
    }
}