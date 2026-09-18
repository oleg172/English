package ru.olmi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByUserIdAndName(Long userId, String name);

    Optional<Topic> findByIdAndUserId(Long id, Long userId);
}
