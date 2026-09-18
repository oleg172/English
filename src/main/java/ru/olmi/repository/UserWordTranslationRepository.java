package ru.olmi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.UserWordTranslation;

@Repository
public interface UserWordTranslationRepository extends JpaRepository<UserWordTranslation, Long> {

    @Query("""
            select uwt
            from UserWordTranslation uwt
            where uwt.userWord.id = :userWordId
            """)
    List<UserWordTranslation> findTranslations(@Param("userWordId") Long userWordId);
}
