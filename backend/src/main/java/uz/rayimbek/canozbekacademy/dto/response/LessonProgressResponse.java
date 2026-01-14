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
public class LessonProgressResponse {
    private Long lessonId;
    private Boolean isCompleted;
    private Integer videoProgress;
    private LocalDateTime completedAt;
    private LocalDateTime lastWatchedAt;
}