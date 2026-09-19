package ru.olmi.service.telegram.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.Word;
import ru.olmi.dto.DictionaryImportRow;
import ru.olmi.dto.DictionaryImportRowResult;
import ru.olmi.dto.enums.DictionaryImportRowStatus;
import ru.olmi.exception.DictionaryImportException;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.service.impl.TranslationFinder;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.storage.UserWordStorage;

@Component
@RequiredArgsConstructor
public class DictionaryImportRowService {

    private final TranslationFinder translationFinder;
    private final UserWordRepository userWordRepository;
    private final UserWordStorage userWordStorage;
    private final TopicStorage topicStorage;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DictionaryImportRowResult importRow(TelegramUser user, DictionaryImportRow row) {
        Word word = translationFinder.findOrCreateWord(row.word(), user.getPreferredFromLanguage(), user.getPreferredToLanguage());

        if (word == null) {
            throw new DictionaryImportException("Не удалось получить перевод.");
        }

        boolean existed = userWordRepository
                .findByUserIdAndWordId(user.getId(), word.getId())
                .isPresent();

        UserWord userWord = userWordStorage.findOrCreate(user, word);

        boolean changed = false;

        for (String translation : row.translations()) {
            if (userWordStorage.addTranslation(user, userWord.getId(), translation)) {
                changed = true;
            }
        }

        for (String topic : row.topics()) {
            if (topicStorage.addTopic(user, userWord.getId(), topic)) {
                changed = true;
            }
        }

        if (!existed) {
            return new DictionaryImportRowResult(DictionaryImportRowStatus.ADDED);
        }

        if (changed) {
            return new DictionaryImportRowResult(DictionaryImportRowStatus.UPDATED);
        }

        return new DictionaryImportRowResult(DictionaryImportRowStatus.UNCHANGED);
    }
}
