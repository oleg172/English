package ru.olmi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "telegram_users")
@Getter
@Setter
@Accessors(chain = true)
public class TelegramUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(name = "preferred_from_language", nullable = false, length = 10)
    private String preferredFromLanguage;

    @Column(name = "preferred_to_language", nullable = false, length = 10)
    private String preferredToLanguage;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @PrePersist
    protected void onCreateUser() {
        lastActivity = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void onUpdateUser() {
        lastActivity = LocalDateTime.now(ZoneOffset.UTC);
    }
}
