package ru.olmi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * Базовая сущность для хранения в БД.
 */
@Getter
@Setter
@ToString
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class BaseEntity {

    /**
     * Дата создания записи.
     */
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime created;

    /**
     * Запись первичных параметров при создании объекта.
     */
    @PrePersist
    void onCreate() {
        if (this.created == null) {
            this.created = LocalDateTime.now(Clock.systemUTC());
        }
    }
}
