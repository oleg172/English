package ru.olmi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.Translation;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
}
