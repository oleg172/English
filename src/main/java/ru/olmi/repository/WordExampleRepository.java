package ru.olmi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.olmi.domain.WordExample;

@Repository
public interface WordExampleRepository extends JpaRepository<WordExample, Long> {
}
