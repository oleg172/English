package ru.olmi.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.domain.enums.UserLearningWordStatus;

@Repository
public interface UserLearningWordRepository extends JpaRepository<UserLearningWord, Long> {

    Optional<UserLearningWord> findByUserWordIdAndUserWordUserId(Long userWordId, Long userId);

    List<UserLearningWord> findAllByUserWordIdInAndUserWordUserId(Collection<Long> userWordIds, Long userId);

    @Query("""
            select distinct ulw
            from UserLearningWord ulw
            join fetch ulw.userWord uw
            join fetch uw.word
            where uw.user.id = :userId
              and ulw.userWord.id in :userWordIds
            """)
    List<UserLearningWord> findForTraining(@Param("userId") Long userId, @Param("userWordIds") Collection<Long> userWordIds);

    @Query("""
        select ulw
        from UserLearningWord ulw
        join fetch ulw.userWord uw
        join fetch uw.word
        where uw.user.id = :userId
          and (
              ulw.status = :newStatus
              or ulw.nextReviewAt <= :now
          )
        order by
            case
                when ulw.status = :newStatus then 1
                else 0
            end,
            ulw.nextReviewAt asc
        """)
    List<UserLearningWord> findReadyForToday(
            @Param("userId") Long userId,
            @Param("newStatus") UserLearningWordStatus newStatus,
            @Param("now") Instant now,
            Pageable pageable
    );
}
