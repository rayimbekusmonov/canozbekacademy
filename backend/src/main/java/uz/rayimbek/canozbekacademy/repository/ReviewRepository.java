package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByCourseIdAndUserId(Long courseId, Long userId);

    List<Review> findByCourseId(Long courseId);

    List<Review> findByUserId(Long userId);

    Boolean existsByCourseIdAndUserId(Long courseId, Long userId);
}