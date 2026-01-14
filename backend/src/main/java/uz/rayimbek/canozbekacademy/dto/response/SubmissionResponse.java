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
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String content;
    private String fileUrls; // JSON
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private Integer score;
    private Integer maxScore;
    private String feedback;
    private String status; // SUBMITTED, GRADED, REVISION_NEEDED
}