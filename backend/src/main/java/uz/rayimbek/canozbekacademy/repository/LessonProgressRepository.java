package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.LessonProgress;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    List<LessonProgress> findByUserId(Long userId);

    List<LessonProgress> findByLessonId(Long lessonId);

    List<LessonProgress> findByUserIdAndIsCompletedTrue(Long userId);

    Long countByUserIdAndIsCompletedTrue(Long userId);
}