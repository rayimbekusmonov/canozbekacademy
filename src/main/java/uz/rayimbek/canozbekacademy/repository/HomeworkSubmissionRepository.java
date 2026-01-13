package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.HomeworkSubmission;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission, Long> {

    Optional<HomeworkSubmission> findByAssignmentIdAndUserId(Long assignmentId, Long userId);

    List<HomeworkSubmission> findByUserId(Long userId);

    List<HomeworkSubmission> findByAssignmentId(Long assignmentId);

    List<HomeworkSubmission> findByStatus(HomeworkSubmission.SubmissionStatus status);
}