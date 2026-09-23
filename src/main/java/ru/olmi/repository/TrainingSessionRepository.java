package ru.olmi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.enums.TrainingSessionStatus;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    Optional<TrainingSession> findByIdAndUserId(Long id, Long userId);

    Optional<TrainingSession> findFirstByUserIdAndStatusOrderByStartedAtDesc(Long userId, TrainingSessionStatus status);
}
