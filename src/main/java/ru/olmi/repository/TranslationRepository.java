package ru.olmi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.Translation;
import ru.olmi.domain.Word;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    @Query("""
            select distinct t.word
            from Translation t
            where t.word.fromLanguage = :fromLanguage
              and t.word.toLanguage = :toLanguage
              and lower(t.translation) = lower(:translation)
            """)
    List<Word> findWordsByTranslation(
            @Param("translation") String translation,
            @Param("fromLanguage") String fromLanguage,
            @Param("toLanguage") String toLanguage
    );
}
