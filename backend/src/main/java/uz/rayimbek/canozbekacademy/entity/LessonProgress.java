package uz.rayimbek.canozbekacademy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column
    @Builder.Default
    private Integer videoProgress = 0; // seconds watched

    private LocalDateTime completedAt;

    @UpdateTimestamp
    private LocalDateTime lastWatchedAt;
}