package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.Course;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySlug(String slug);

    List<Course> findByIsPublishedTrue();

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByCategory(String category);

    List<Course> findByLevel(Course.CourseLevel level);

    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND " +
            "(:category IS NULL OR c.category = :category) AND " +
            "(:level IS NULL OR c.level = :level) AND " +
            "(:language IS NULL OR c.language = :language)")
    List<Course> findByFilters(
            @Param("category") String category,
            @Param("level") Course.CourseLevel level,
            @Param("language") String language
    );

    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Course> searchByTitle(@Param("search") String search);
}