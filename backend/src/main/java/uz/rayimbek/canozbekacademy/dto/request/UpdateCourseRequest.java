package uz.rayimbek.canozbekacademy.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCourseRequest {

    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    private String category;

    @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED", message = "Level must be BEGINNER, INTERMEDIATE, or ADVANCED")
    private String level;

    private String language;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be positive")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount price must be positive")
    private BigDecimal discountPrice;

    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer durationWeeks;

    private Boolean isPublished;
}