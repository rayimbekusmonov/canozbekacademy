package uz.rayimbek.canozbekacademy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.rayimbek.canozbekacademy.dto.request.CreateCourseRequest;
import uz.rayimbek.canozbekacademy.dto.request.UpdateCourseRequest;
import uz.rayimbek.canozbekacademy.dto.response.CourseDetailResponse;
import uz.rayimbek.canozbekacademy.dto.response.CourseResponse;
import uz.rayimbek.canozbekacademy.dto.response.MessageResponse;
import uz.rayimbek.canozbekacademy.security.UserPrincipal;
import uz.rayimbek.canozbekacademy.service.CourseService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Get all published courses
     * GET /api/v1/courses
     */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String language
    ) {
        log.info("Get all courses - search: {}, category: {}, level: {}, language: {}",
                search, category, level, language);

        List<CourseResponse> courses;

        if (search != null && !search.isEmpty()) {
            courses = courseService.searchCourses(search);
        } else if (category != null || level != null || language != null) {
            courses = courseService.filterCourses(category, level, language);
        } else {
            courses = courseService.getAllCourses();
        }

        return ResponseEntity.ok(courses);
    }

    /**
     * Get course by ID
     * GET /api/v1/courses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable Long id) {
        log.info("Get course by id: {}", id);
        CourseDetailResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Get course by slug
     * GET /api/v1/courses/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CourseDetailResponse> getCourseBySlug(@PathVariable String slug) {
        log.info("Get course by slug: {}", slug);
        CourseDetailResponse course = courseService.getCourseBySlug(slug);
        return ResponseEntity.ok(course);
    }

    /**
     * Create new course (Teacher/Admin only)
     * POST /api/v1/courses
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Create course request from user: {}", currentUser.getEmail());
        CourseResponse course = courseService.createCourse(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    /**
     * Update course
     * PUT /api/v1/courses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Update course {} by user: {}", id, currentUser.getEmail());
        CourseResponse course = courseService.updateCourse(id, request, currentUser.getId());
        return ResponseEntity.ok(course);
    }

    /**
     * Delete course
     * DELETE /api/v1/courses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<MessageResponse> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Delete course {} by user: {}", id, currentUser.getEmail());
        courseService.deleteCourse(id, currentUser.getId());
        return ResponseEntity.ok(new MessageResponse("Course deleted successfully"));
    }

    /**
     * Publish/unpublish course
     * PATCH /api/v1/courses/{id}/publish
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<CourseResponse> togglePublish(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Toggle publish for course {} by user: {}", id, currentUser.getEmail());
        CourseResponse course = courseService.togglePublish(id, currentUser.getId());
        return ResponseEntity.ok(course);
    }

    /**
     * Get courses by teacher
     * GET /api/v1/courses/teacher/{teacherId}
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByTeacher(@PathVariable Long teacherId) {
        log.info("Get courses by teacher: {}", teacherId);
        List<CourseResponse> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get my courses (for logged in teacher)
     * GET /api/v1/courses/my-courses
     */
    @GetMapping("/my-courses")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        log.info("Get my courses for user: {}", currentUser.getEmail());
        List<CourseResponse> courses = courseService.getCoursesByTeacher(currentUser.getId());
        return ResponseEntity.ok(courses);
    }
}