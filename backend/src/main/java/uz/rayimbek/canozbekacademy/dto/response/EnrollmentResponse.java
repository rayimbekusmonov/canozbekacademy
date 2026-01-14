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
public class EnrollmentResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseSlug;
    private String courseThumbnailUrl;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private BigDecimal progressPercentage;
    private String certificateUrl;
}