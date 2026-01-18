package uz.rayimbek.canozbekacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rayimbek.canozbekacademy.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);
}