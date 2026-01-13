package uz.rayimbek.canozbekacademy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_courses_slug", columnList = "slug"),
        @Index(name = "idx_courses_teacher", columnList = "teacher_id"),
        @Index(name = "idx_courses_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false, length = 100)
    private String category; // Turkish Language, Math, English, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CourseLevel level;

    @Column(nullable = false, length = 50)
    private String language; // Turkish, English, Russian

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column
    private Integer durationWeeks;

    @Column
    private Integer totalLessons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Integer totalStudents = 0;

    @Column
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<CourseSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();

    public enum CourseLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }

    // Helper methods
    public void addSection(CourseSection section) {
        sections.add(section);
        section.setCourse(this);
    }

    public void removeSection(CourseSection section) {
        sections.remove(section);
        section.setCourse(null);
    }

    public void updateRating() {
        if (totalReviews > 0 && reviews != null && !reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            this.rating = BigDecimal.valueOf(avgRating);
        }
    }

    public boolean isFree() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getEffectivePrice() {
        return discountPrice != null ? discountPrice : price;
    }
}