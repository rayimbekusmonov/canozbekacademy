package uz.rayimbek.canozbekacademy.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.rayimbek.canozbekacademy.entity.HomeworkSubmission;

@Data
public class GradeSubmissionRequest {

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be positive")
    private Integer score;

    @NotBlank(message = "Feedback is required")
    private String feedback;

    @NotNull(message = "Status is required")
    private HomeworkSubmission.SubmissionStatus status; // GRADED or REVISION_NEEDED
}