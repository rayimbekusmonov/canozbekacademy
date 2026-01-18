package uz.rayimbek.canozbekacademy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework_submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assignment_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeworkSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String fileUrls;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    private LocalDateTime gradedAt;
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    public enum SubmissionStatus {
        SUBMITTED, GRADED, REVISION_NEEDED
    }
}