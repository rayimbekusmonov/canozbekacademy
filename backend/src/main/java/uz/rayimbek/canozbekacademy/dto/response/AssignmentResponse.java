package uz.rayimbek.canozbekacademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private String title;
    private String description;
    private Integer maxScore;
    private Integer dueDays;
    private LocalDateTime createdAt;
    private Boolean hasSubmission;
    private SubmissionResponse submission;
}