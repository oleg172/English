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
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTopic;

@Repository
public interface UserWordTopicRepository extends JpaRepository<UserWordTopic, Long> {

    @Query("""
            select uwt
            from UserWordTopic uwt
            join fetch uwt.topic
            where uwt.userWord.id = :userWordId
              and uwt.userWord.user.id = :userId
            """)
    List<UserWordTopic> findTopics(
            @Param("userWordId") Long userWordId,
            @Param("userId") Long userId
    );

    @Query("""
            select uwt
            from UserWordTopic uwt
            join fetch uwt.userWord uw
            where uwt.id = :userWordTopicId
              and uw.id = :userWordId
              and uw.user.id = :userId
            """)
    Optional<UserWordTopic> findByIdAndUserWordIdAndUserId(
            @Param("userWordTopicId") Long userWordTopicId,
            @Param("userWordId") Long userWordId,
            @Param("userId") Long userId
    );

    @Query("""
            select uwt.topic
            from UserWordTopic uwt
            where uwt.userWord.user.id = :userId
            group by uwt.topic
            order by uwt.topic.name
            """)
    Page<Topic> findTopicsForUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            select uw
            from UserWordTopic uwt
            join uwt.userWord uw
            join fetch uw.word
            where uwt.topic.id = :topicId
              and uw.user.id = :userId
            order by uw.word.word
            """)
    Page<UserWord> findWordsByTopic(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId,
            Pageable pageable
    );

    @Query("""
            select uwt.topic
            from UserWordTopic uwt
            where uwt.userWord.user.id = :userId
              and lower(uwt.topic.name) like lower(concat('%', :query, '%'))
            group by uwt.topic
            order by uwt.topic.name
            """)
    Page<Topic> searchTopicsForUser(
            @Param("userId") Long userId,
            @Param("query") String query,
            Pageable pageable
    );

    @Query("""
            select uw
            from UserWordTopic uwt
            join uwt.userWord uw
            join fetch uw.word
            where uwt.topic.id = :topicId
              and uw.user.id = :userId
              and lower(uw.word.word) like lower(concat('%', :query, '%'))
            order by uw.word.word
            """)
    Page<UserWord> searchWordsByTopic(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId,
            @Param("query") String query,
            Pageable pageable
    );

    @Query("""
            select distinct t
            from UserWordTopic t
            join fetch t.topic
            where t.userWord.id in :userWordIds
            order by t.userWord.id, t.id
            """)
    List<UserWordTopic> findAllForExport(@Param("userWordIds") Collection<Long> userWordIds);

    @Query("""
            select uw
            from UserWordTopic uwt
            join uwt.userWord uw
            join fetch uw.word
            where uwt.topic.id = :topicId
              and uw.user.id = :userId
              and uw.id in :userWordIds
            order by uw.word.word
            """)
    List<UserWord> findWordsByTopicAndIds(@Param("userId") Long userId, @Param("topicId") Long topicId, @Param("userWordIds") Collection<Long> userWordIds);

    @Query("""
            select uw
            from UserWordTopic uwt
            join uwt.userWord uw
            join fetch uw.word
            where uwt.topic.id = :topicId
              and uw.user.id = :userId
            order by uw.word.word
            """)
    List<UserWord> findAllWordsByTopic(@Param("userId") Long userId, @Param("topicId") Long topicId);
}
