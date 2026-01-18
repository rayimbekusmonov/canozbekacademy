package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.Assignment;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByLessonId(Long lessonId);
}