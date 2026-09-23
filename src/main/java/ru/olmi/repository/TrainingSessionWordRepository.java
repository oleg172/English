package ru.olmi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.TrainingSessionWord;

@Repository
public interface TrainingSessionWordRepository extends JpaRepository<TrainingSessionWord, Long> {

    @Query("""
            select tsw
            from TrainingSessionWord tsw
            join fetch tsw.userLearningWord ulw
            join fetch ulw.userWord uw
            join fetch uw.word
            where tsw.trainingSession.id = :sessionId
              and tsw.trainingSession.user.id = :userId
            order by tsw.position
            """)
    List<TrainingSessionWord> findAllBySessionIdAndUserId(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    @Query("""
            select tsw
            from TrainingSessionWord tsw
            join fetch tsw.trainingSession ts
            where tsw.id = :sessionWordId
              and ts.user.id = :userId
            """)
    Optional<TrainingSessionWord> findByIdAndUserId(@Param("sessionWordId") Long sessionWordId, @Param("userId") Long userId);
}
