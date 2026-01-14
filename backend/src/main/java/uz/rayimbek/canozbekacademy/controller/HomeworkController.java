package uz.rayimbek.canozbekacademy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.rayimbek.canozbekacademy.dto.request.GradeSubmissionRequest;
import uz.rayimbek.canozbekacademy.dto.request.SubmitHomeworkRequest;
import uz.rayimbek.canozbekacademy.dto.response.AssignmentResponse;
import uz.rayimbek.canozbekacademy.dto.response.SubmissionResponse;
import uz.rayimbek.canozbekacademy.security.UserPrincipal;
import uz.rayimbek.canozbekacademy.service.HomeworkService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    /**
     * Get assignment by ID
     * GET /api/v1/homework/assignments/{id}
     */
    @GetMapping("/assignments/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<AssignmentResponse> getAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get assignment {} for user {}", id, currentUser.getEmail());
        AssignmentResponse assignment = homeworkService.getAssignment(id, currentUser.getId());
        return ResponseEntity.ok(assignment);
    }

    /**
     * Submit homework
     * POST /api/v1/homework/assignments/{id}/submit
     */
    @PostMapping("/assignments/{id}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<SubmissionResponse> submitHomework(
            @PathVariable Long id,
            @Valid @RequestBody SubmitHomeworkRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Submit homework for assignment {} by user {}", id, currentUser.getEmail());
        SubmissionResponse submission = homeworkService.submitHomework(
                id, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    /**
     * Get submission by ID
     * GET /api/v1/homework/submissions/{id}
     */
    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get submission {} for user {}", id, currentUser.getEmail());
        SubmissionResponse submission = homeworkService.getSubmission(id, currentUser.getId());
        return ResponseEntity.ok(submission);
    }

    /**
     * Grade submission (Teacher/Admin only)
     * PUT /api/v1/homework/submissions/{id}/grade
     */
    @PutMapping("/submissions/{id}/grade")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            @PathVariable Long id,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Grade submission {} by teacher {}", id, currentUser.getEmail());
        SubmissionResponse submission = homeworkService.gradeSubmission(
                id, currentUser.getId(), request);
        return ResponseEntity.ok(submission);
    }

    /**
     * Get all submissions for an assignment (Teacher only)
     * GET /api/v1/homework/assignments/{id}/submissions
     */
    @GetMapping("/assignments/{id}/submissions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get submissions for assignment {} by teacher {}", id, currentUser.getEmail());
        List<SubmissionResponse> submissions = homeworkService.getSubmissionsByAssignment(
                id, currentUser.getId());
        return ResponseEntity.ok(submissions);
    }

    /**
     * Get my submissions
     * GET /api/v1/homework/my-submissions
     */
    @GetMapping("/my-submissions")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<SubmissionResponse>> getMySubmissions(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get submissions for user {}", currentUser.getEmail());
        List<SubmissionResponse> submissions = homeworkService.getMySubmissions(currentUser.getId());
        return ResponseEntity.ok(submissions);
    }

    /**
     * Get pending submissions (Teacher)
     * GET /api/v1/homework/pending
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<SubmissionResponse>> getPendingSubmissions(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get pending submissions for teacher {}", currentUser.getEmail());
        List<SubmissionResponse> submissions = homeworkService.getPendingSubmissions(currentUser.getId());
        return ResponseEntity.ok(submissions);
    }
}