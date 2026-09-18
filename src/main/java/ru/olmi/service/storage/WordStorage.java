package ru.olmi.service.storage;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.Translation;
import ru.olmi.domain.Word;
import ru.olmi.domain.WordExample;
import ru.olmi.dto.TranslationResult;
import ru.olmi.repository.WordRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class WordStorage {

    private final WordRepository wordRepository;

    public Word save(TranslationResult result, String fromLanguage, String toLanguage) {
        Word word = new Word()
                .setWord(result.getWord())
                .setFromLanguage(fromLanguage)
                .setToLanguage(toLanguage)
                .setTranscription(result.getTranscription());

        List<Translation> translations = safeList(result.getCommonTranslations())
                .stream()
                .map(value -> new Translation()
                        .setWord(word)
                        .setTranslation(value)
                )
                .toList();

        List<WordExample> examples = safeList(result.getUsageExamples())
                .stream()
                .map(value -> new WordExample()
                        .setWord(word)
                        .setExample(value)
                )
                .toList();

        word.setTranslations(translations);
        word.setExamples(examples);

        return wordRepository.save(word);
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null
               ? List.of()
               : values;
    }
}
