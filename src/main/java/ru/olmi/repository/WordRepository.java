package ru.olmi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByWordAndFromLanguageAndToLanguage(
            String word,
            String fromLanguage,
            String toLanguage
    );

    @Query("""
        select distinct w
        from Word w
        join w.translations t
        where t.translation = :translation
          and w.fromLanguage = :fromLanguage
          and w.toLanguage = :toLanguage
        """)
    Optional<Word> findByTranslation(
            @Param("translation") String translation,
            @Param("fromLanguage") String fromLanguage,
            @Param("toLanguage") String toLanguage
    );
}
