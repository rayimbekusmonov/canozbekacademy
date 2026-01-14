package uz.rayimbek.canozbekacademy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rayimbek.canozbekacademy.dto.request.GradeSubmissionRequest;
import uz.rayimbek.canozbekacademy.dto.request.SubmitHomeworkRequest;
import uz.rayimbek.canozbekacademy.dto.response.AssignmentResponse;
import uz.rayimbek.canozbekacademy.dto.response.SubmissionResponse;
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
public class HomeworkService {

    private final AssignmentRepository assignmentRepository;
    private final HomeworkSubmissionRepository submissionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Get assignment by ID
     */
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignment(Long assignmentId, Long userId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check enrollment
        Course course = assignment.getLesson().getSection().getCourse();
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, course.getId());

        if (!isEnrolled) {
            throw new UnauthorizedException("You must be enrolled to view this assignment");
        }

        // Get user's submission if exists
        HomeworkSubmission submission = submissionRepository
                .findByAssignmentIdAndUserId(assignmentId, userId)
                .orElse(null);

        return mapToAssignmentResponse(assignment, submission);
    }

    /**
     * Submit homework
     */
    @Transactional
    public SubmissionResponse submitHomework(
            Long assignmentId,
            Long userId,
            SubmitHomeworkRequest request
    ) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check enrollment
        Course course = assignment.getLesson().getSection().getCourse();
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, course.getId());

        if (!isEnrolled) {
            throw new UnauthorizedException("You must be enrolled to submit homework");
        }

        // Check if already submitted
        HomeworkSubmission existingSubmission = submissionRepository
                .findByAssignmentIdAndUserId(assignmentId, userId)
                .orElse(null);

        if (existingSubmission != null && existingSubmission.getStatus() == HomeworkSubmission.SubmissionStatus.GRADED) {
            throw new BadRequestException("This homework has already been graded. Cannot resubmit.");
        }

        HomeworkSubmission submission;
        if (existingSubmission != null) {
            // Update existing submission
            submission = existingSubmission;
            submission.setContent(request.getContent());
            submission.setFileUrls(request.getFileUrls());
            submission.setSubmittedAt(LocalDateTime.now());
            submission.setStatus(HomeworkSubmission.SubmissionStatus.SUBMITTED);
            submission.setGradedAt(null);
            submission.setScore(null);
            submission.setFeedback(null);
        } else {
            // Create new submission
            submission = HomeworkSubmission.builder()
                    .assignment(assignment)
                    .user(user)
                    .content(request.getContent())
                    .fileUrls(request.getFileUrls())
                    .status(HomeworkSubmission.SubmissionStatus.SUBMITTED)
                    .build();
        }

        submission = submissionRepository.save(submission);
        log.info("Homework submitted for assignment {} by user {}", assignmentId, userId);

        return mapToSubmissionResponse(submission);
    }

    /**
     * Grade submission (Teacher/Admin only)
     */
    @Transactional
    public SubmissionResponse gradeSubmission(
            Long submissionId,
            Long teacherId,
            GradeSubmissionRequest request
    ) {
        HomeworkSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        // Check if teacher owns this course or is admin
        Course course = submission.getAssignment().getLesson().getSection().getCourse();
        if (!course.getTeacher().getId().equals(teacherId) && !teacher.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to grade this submission");
        }

        // Validate score
        if (request.getScore() < 0 || request.getScore() > submission.getAssignment().getMaxScore()) {
            throw new BadRequestException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(request.getStatus());
        submission.setGradedAt(LocalDateTime.now());

        submission = submissionRepository.save(submission);
        log.info("Submission {} graded by teacher {}: {}/{}",
                submissionId, teacherId, request.getScore(), submission.getAssignment().getMaxScore());

        // TODO: Send notification to student

        return mapToSubmissionResponse(submission);
    }

    /**
     * Get submission by ID
     */
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(Long submissionId, Long userId) {
        HomeworkSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check access: must be the student or the teacher
        Course course = submission.getAssignment().getLesson().getSection().getCourse();
        boolean isStudent = submission.getUser().getId().equals(userId);
        boolean isTeacher = course.getTeacher().getId().equals(userId);
        boolean isAdmin = user.isAdmin();

        if (!isStudent && !isTeacher && !isAdmin) {
            throw new UnauthorizedException("You don't have permission to view this submission");
        }

        return mapToSubmissionResponse(submission);
    }

    /**
     * Get all submissions for an assignment (Teacher only)
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        // Check if teacher owns this course
        Course course = assignment.getLesson().getSection().getCourse();
        if (!course.getTeacher().getId().equals(teacherId) && !teacher.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view these submissions");
        }

        List<HomeworkSubmission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissions.stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get my submissions
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getMySubmissions(Long userId) {
        List<HomeworkSubmission> submissions = submissionRepository.findByUserId(userId);
        return submissions.stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pending submissions (Teacher)
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getPendingSubmissions(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        if (!teacher.isTeacher() && !teacher.isAdmin()) {
            throw new UnauthorizedException("Only teachers can view pending submissions");
        }

        List<HomeworkSubmission> submissions = submissionRepository
                .findByStatus(HomeworkSubmission.SubmissionStatus.SUBMITTED);

        // Filter by teacher's courses
        return submissions.stream()
                .filter(s -> {
                    Course course = s.getAssignment().getLesson().getSection().getCourse();
                    return course.getTeacher().getId().equals(teacherId) || teacher.isAdmin();
                })
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

    private AssignmentResponse mapToAssignmentResponse(Assignment assignment, HomeworkSubmission submission) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .lessonId(assignment.getLesson().getId())
                .lessonTitle(assignment.getLesson().getTitle())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .maxScore(assignment.getMaxScore())
                .dueDays(assignment.getDueDays())
                .createdAt(assignment.getCreatedAt())
                .hasSubmission(submission != null)
                .submission(submission != null ? mapToSubmissionResponse(submission) : null)
                .build();
    }

    private SubmissionResponse mapToSubmissionResponse(HomeworkSubmission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .studentId(submission.getUser().getId())
                .studentName(submission.getUser().getFullName())
                .content(submission.getContent())
                .fileUrls(submission.getFileUrls())
                .submittedAt(submission.getSubmittedAt())
                .gradedAt(submission.getGradedAt())
                .score(submission.getScore())
                .maxScore(submission.getAssignment().getMaxScore())
                .feedback(submission.getFeedback())
                .status(submission.getStatus().name())
                .build();
    }
}