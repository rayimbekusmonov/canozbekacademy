package uz.rayimbek.canozbekacademy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rayimbek.canozbekacademy.dto.request.CreateCourseRequest;
import uz.rayimbek.canozbekacademy.dto.request.UpdateCourseRequest;
import uz.rayimbek.canozbekacademy.dto.response.CourseDetailResponse;
import uz.rayimbek.canozbekacademy.dto.response.CourseResponse;
import uz.rayimbek.canozbekacademy.entity.Course;
import uz.rayimbek.canozbekacademy.entity.CourseSection;
import uz.rayimbek.canozbekacademy.entity.User;
import uz.rayimbek.canozbekacademy.exception.BadRequestException;
import uz.rayimbek.canozbekacademy.exception.ResourceNotFoundException;
import uz.rayimbek.canozbekacademy.exception.UnauthorizedException;
import uz.rayimbek.canozbekacademy.repository.CourseRepository;
import uz.rayimbek.canozbekacademy.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Get all published courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findByIsPublishedTrue();
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get course by ID
     */
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        return mapToCourseDetailResponse(course);
    }

    /**
     * Get course by slug
     */
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "slug", slug));

        return mapToCourseDetailResponse(course);
    }

    /**
     * Search courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> searchCourses(String keyword) {
        List<Course> courses = courseRepository.searchByTitle(keyword);
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filter courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> filterCourses(
            String category,
            String level,
            String language
    ) {
        Course.CourseLevel courseLevel = level != null ?
                Course.CourseLevel.valueOf(level.toUpperCase()) : null;

        List<Course> courses = courseRepository.findByFilters(category, courseLevel, language);
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create new course (Teacher/Admin only)
     */
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        if (!teacher.isTeacher() && !teacher.isAdmin()) {
            throw new UnauthorizedException("Only teachers and admins can create courses");
        }

        // Generate slug from title
        String slug = generateSlug(request.getTitle());

        // Check if slug already exists
        if (courseRepository.findBySlug(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .category(request.getCategory())
                .level(Course.CourseLevel.valueOf(request.getLevel().toUpperCase()))
                .language(request.getLanguage())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .durationWeeks(request.getDurationWeeks())
                .teacher(teacher)
                .isPublished(false)
                .build();

        course = courseRepository.save(course);
        log.info("Course created: {} by teacher: {}", course.getTitle(), teacher.getEmail());

        return mapToCourseResponse(course);
    }

    /**
     * Update course
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user is the teacher or admin
        if (!course.getTeacher().getId().equals(userId) && !user.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this course");
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
            course.setSlug(generateSlug(request.getTitle()));
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            course.setCategory(request.getCategory());
        }
        if (request.getLevel() != null) {
            course.setLevel(Course.CourseLevel.valueOf(request.getLevel().toUpperCase()));
        }
        if (request.getLanguage() != null) {
            course.setLanguage(request.getLanguage());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getDiscountPrice() != null) {
            course.setDiscountPrice(request.getDiscountPrice());
        }
        if (request.getDurationWeeks() != null) {
            course.setDurationWeeks(request.getDurationWeeks());
        }
        if (request.getIsPublished() != null) {
            course.setIsPublished(request.getIsPublished());
        }

        course = courseRepository.save(course);
        log.info("Course updated: {}", course.getTitle());

        return mapToCourseResponse(course);
    }

    /**
     * Delete course
     */
    @Transactional
    public void deleteCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user is the teacher or admin
        if (!course.getTeacher().getId().equals(userId) && !user.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to delete this course");
        }

        courseRepository.delete(course);
        log.info("Course deleted: {} by user: {}", course.getTitle(), user.getEmail());
    }

    /**
     * Publish/unpublish course
     */
    @Transactional
    public CourseResponse togglePublish(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!course.getTeacher().getId().equals(userId) && !user.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to publish this course");
        }

        course.setIsPublished(!course.getIsPublished());
        course = courseRepository.save(course);

        log.info("Course {} {}", course.getTitle(),
                course.getIsPublished() ? "published" : "unpublished");

        return mapToCourseResponse(course);
    }

    /**
     * Get courses by teacher
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .category(course.getCategory())
                .level(course.getLevel().name())
                .language(course.getLanguage())
                .price(course.getPrice())
                .discountPrice(course.getDiscountPrice())
                .durationWeeks(course.getDurationWeeks())
                .totalLessons(course.getTotalLessons())
                .teacherName(course.getTeacher().getFullName())
                .teacherId(course.getTeacher().getId())
                .rating(course.getRating())
                .totalStudents(course.getTotalStudents())
                .totalReviews(course.getTotalReviews())
                .isPublished(course.getIsPublished())
                .createdAt(course.getCreatedAt())
                .build();
    }

    private CourseDetailResponse mapToCourseDetailResponse(Course course) {
        CourseDetailResponse response = new CourseDetailResponse();

        // Copy basic fields from CourseResponse
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setSlug(course.getSlug());
        response.setDescription(course.getDescription());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setCategory(course.getCategory());
        response.setLevel(course.getLevel().name());
        response.setLanguage(course.getLanguage());
        response.setPrice(course.getPrice());
        response.setDiscountPrice(course.getDiscountPrice());
        response.setDurationWeeks(course.getDurationWeeks());
        response.setTotalLessons(course.getTotalLessons());
        response.setTeacherName(course.getTeacher().getFullName());
        response.setTeacherId(course.getTeacher().getId());
        response.setRating(course.getRating());
        response.setTotalStudents(course.getTotalStudents());
        response.setTotalReviews(course.getTotalReviews());
        response.setIsPublished(course.getIsPublished());
        response.setCreatedAt(course.getCreatedAt());

        // Add sections (we'll implement this later)
        // response.setSections(...);

        return response;
    }
}