package uz.rayimbek.canozbekacademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private String category;
    private String level;
    private String language;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer durationWeeks;
    private Integer totalLessons;
    private String teacherName;
    private Long teacherId;
    private BigDecimal rating;
    private Integer totalStudents;
    private Integer totalReviews;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private String teacherAvatar;
    private String teacherSpecialization;
    private Double averageRating;
}