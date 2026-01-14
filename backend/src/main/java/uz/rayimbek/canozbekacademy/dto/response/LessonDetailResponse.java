package uz.rayimbek.canozbekacademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private Integer videoDuration;
    private String formattedDuration;
    private Integer orderIndex;
    private Boolean isPreview;
    private Boolean isLocked;
    private Boolean isCompleted;
    private Integer videoProgress;
    private String resources; // JSON
    private Boolean hasAssignments;
    private Integer assignmentsCount;
}
