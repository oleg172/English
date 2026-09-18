package ru.olmi.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(
        name = "words",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_words_word_languages",
                columnNames = {
                        "word",
                        "from_language",
                        "to_language"
                }
        )
)
@Getter
@Setter
@Accessors(chain = true)
public class Word extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "from_language", nullable = false, length = 10)
    private String fromLanguage;

    @Column(name = "to_language", nullable = false, length = 10)
    private String toLanguage;

    @Column(name = "transcription", length = 1000)
    private String transcription;

    @OneToMany(
            mappedBy = "word",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Translation> translations = new ArrayList<>();

    @OneToMany(
            mappedBy = "word",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<WordExample> examples = new ArrayList<>();
}
