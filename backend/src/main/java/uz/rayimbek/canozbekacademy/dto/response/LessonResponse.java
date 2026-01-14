package uz.rayimbek.canozbekacademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long id;
    private String title;
    private String description;
    private Integer videoDuration;
    private String formattedDuration;
    private Integer orderIndex;
    private Boolean isPreview;
    private Boolean isLocked;
    private Boolean isCompleted;
}