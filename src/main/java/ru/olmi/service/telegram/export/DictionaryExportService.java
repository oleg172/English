package ru.olmi.service.telegram.export;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.dto.DictionaryCsvRow;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.repository.UserWordTopicRepository;
import ru.olmi.repository.UserWordTranslationRepository;
import ru.olmi.service.telegram.TelegramApi;

@Component
@RequiredArgsConstructor
public class DictionaryExportService {

    private final UserWordRepository userWordRepository;
    private final UserWordTranslationRepository userWordTranslationRepository;
    private final UserWordTopicRepository userWordTopicRepository;
    private final DictionaryExportCsvWriter csvWriter;
    private final TelegramApi telegramApi;

    @Transactional(readOnly = true)
    public void export(TelegramUser user, Long chatId) {
        List<DictionaryCsvRow> rows = findRows(user);

        String csv = csvWriter.write(rows);
        telegramApi.sendDocument(chatId, csv.getBytes(StandardCharsets.UTF_8), "dictionary.csv");
    }

    private List<DictionaryCsvRow> findRows(TelegramUser user) {
        List<UserWord> userWords = userWordRepository.findAllForExport(user.getId());

        if (userWords.isEmpty()) {
            return List.of();
        }

        List<Long> userWordIds = userWords.stream()
                                          .map(UserWord::getId)
                                          .toList();

        Map<Long, List<String>> translations = userWordTranslationRepository.findAllForExport(userWordIds)
                                                                            .stream()
                                                                            .collect(Collectors.groupingBy(translation -> translation.getUserWord().getId(),
                                                                                    LinkedHashMap::new,
                                                                                    Collectors.mapping(UserWordTranslation::getTranslation,
                                                                                            Collectors.toList())));

        Map<Long, List<String>> topics = userWordTopicRepository.findAllForExport(userWordIds)
                                                                .stream()
                                                                .collect(Collectors.groupingBy(userWordTopic -> userWordTopic.getUserWord().getId(),
                                                                        LinkedHashMap::new,
                                                                        Collectors.mapping(userWordTopic -> userWordTopic.getTopic().getName(),
                                                                                Collectors.toList())));

        return userWords.stream()
                        .map(userWord -> new DictionaryCsvRow(userWord.getWord().getWord(), translations.getOrDefault(userWord.getId(), List.of()),
                                topics.getOrDefault(userWord.getId(), List.of())
                        ))
                        .toList();
    }
}
