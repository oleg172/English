package ru.olmi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(
        name = "user_word_translations",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_word_translations",
                columnNames = {
                        "user_word_id",
                        "translation"
                }
        )
)
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserWordTranslation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_word_id", nullable = false)
    private UserWord userWord;

    @Column(name = "translation", nullable = false, length = 1000)
    private String translation;
}
