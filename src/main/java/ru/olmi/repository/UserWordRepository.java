package ru.olmi.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTranslation;

@Repository
public interface UserWordRepository extends JpaRepository<UserWord, Long> {

    Optional<UserWord> findByUserIdAndWordId(Long userId, Long wordId);

    @Query("""
            select uw
            from UserWord uw
            join fetch uw.word
            where uw.user.id = :userId
            order by uw.id desc
            """)
    Page<UserWord> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    @Query("""
            select uw
            from UserWord uw
            join fetch uw.word
            where uw.user.id = :userId
              and lower(uw.word.word) like lower(concat('%', :query, '%'))
            order by uw.word.word
            """)
    Page<UserWord> search(
            @Param("userId") Long userId,
            @Param("query") String query,
            Pageable pageable);

    @Query("""
            select uw
            from UserWord uw
            join fetch uw.word
            where uw.id = :userWordId
              and uw.user.id = :userId
            """)
    Optional<UserWord> findByIdAndUserIdWithWord(
            @Param("userWordId") Long userWordId,
            @Param("userId") Long userId);

    @Query("""
            select t
            from UserWordTranslation t
            join fetch t.userWord uw
            where t.id = :translationId
              and uw.user.id = :userId
            """)
    Optional<UserWordTranslation> findTranslationByIdAndUserId(
            @Param("translationId") Long translationId,
            @Param("userId") Long userId);

    @Query("""
            select distinct uw
            from UserWord uw
            left join fetch uw.translations
            where uw.id = :userWordId
              and uw.user.id = :userId
            """)
    Optional<UserWord> findByIdAndUserIdWithTranslations(
            @Param("userWordId") Long userWordId,
            @Param("userId") Long userId
    );

    Optional<UserWord> findByIdAndUserId(
            Long id,
            Long userId
    );

    @Query("""
            select distinct uw
            from UserWord uw
            join fetch uw.word w
            join uw.translations t
            where uw.user.id = :userId
              and w.fromLanguage = :fromLanguage
              and w.toLanguage = :toLanguage
              and lower(t.translation) = lower(:translation)
            """)
    List<UserWord> findByUserTranslation(
            @Param("userId") Long userId,
            @Param("translation") String translation,
            @Param("fromLanguage") String fromLanguage,
            @Param("toLanguage") String toLanguage
    );

    @Query("""
            select uw
            from UserWord uw
            join fetch uw.word
            where uw.user.id = :userId
            order by uw.id
            """)
    List<UserWord> findAllForExport(@Param("userId") Long userId);

    @Query("""
            select uw
            from UserWord uw
            join fetch uw.word
            where uw.user.id = :userId
              and uw.id not in :excludedUserWordIds
            order by uw.word.word
            """)
    List<UserWord> findAllForTraining(@Param("userId") Long userId, @Param("excludedUserWordIds") Collection<Long> excludedUserWordIds);

}
