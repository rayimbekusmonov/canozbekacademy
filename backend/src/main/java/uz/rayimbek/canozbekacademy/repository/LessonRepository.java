package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

    List<Lesson> findByIsPreviewTrue();
}
