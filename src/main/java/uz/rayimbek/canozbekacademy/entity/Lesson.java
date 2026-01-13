package uz.rayimbek.canozbekacademy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lessons", indexes = {
        @Index(name = "idx_lessons_section", columnList = "section_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String videoUrl;

    @Column
    private Integer videoDuration; // in seconds

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPreview = false; // Free preview lesson

    @Column(columnDefinition = "TEXT")
    private String resources; // JSON array of downloadable resources

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Assignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<LessonProgress> progress = new HashSet<>();

    // Helper methods
    public String getFormattedDuration() {
        if (videoDuration == null) return "00:00";
        int minutes = videoDuration / 60;
        int seconds = videoDuration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isLocked(User user) {
        if (isPreview) return false;
        if (user == null) return true;

        // Check if user is enrolled in the course
        boolean isEnrolled = section.getCourse().getEnrollments().stream()
                .anyMatch(e -> e.getUser().getId().equals(user.getId()));

        if (!isEnrolled) return true;

        // Check if previous lessons are completed
        return !isPreviousLessonsCompleted(user);
    }

    private boolean isPreviousLessonsCompleted(User user) {
        // Get all lessons in the same section before this one
        return section.getLessons().stream()
                .filter(l -> l.getOrderIndex() < this.orderIndex)
                .allMatch(l -> l.isCompletedBy(user));
    }

    public boolean isCompletedBy(User user) {
        if (user == null) return false;
        return progress.stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()) && p.getIsCompleted());
    }
}