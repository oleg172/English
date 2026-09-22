package ru.olmi.service.telegram.upload;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DictionaryImportResult;
import ru.olmi.dto.DictionaryCsvRow;
import ru.olmi.dto.DictionaryImportRowResult;

@Component
@RequiredArgsConstructor
public class DictionaryImportService {

    private final DictionaryImportRowService rowService;

    public DictionaryImportResult importWords(TelegramUser user, List<DictionaryCsvRow> rows) {
        DictionaryImportResult result = new DictionaryImportResult();

        for (DictionaryCsvRow row : mergeRows(rows)) {
            result.setProcessed(result.getProcessed() + 1);

            try {
                DictionaryImportRowResult rowResult = rowService.importRow(user, row);

                updateResult(result, rowResult);
            } catch (Exception e) {
                result.setErrors(result.getErrors() + 1);
                result.getErrorMessages().add("Слово '" + row.word() + "': " + errorMessage(e));
            }
        }

        return result;
    }

    private List<DictionaryCsvRow> mergeRows(List<DictionaryCsvRow> rows) {
        Map<String, DictionaryCsvRow> merged = new LinkedHashMap<>();

        for (DictionaryCsvRow row : rows) {
            String key = row.word().toLowerCase(Locale.ROOT);

            DictionaryCsvRow existing = merged.get(key);

            if (existing == null) {
                merged.put(key, row);
                continue;
            }

            merged.put(key, new DictionaryCsvRow(existing.word(), mergeValues(existing.translations(), row.translations()),
                            mergeValues(existing.topics(), row.topics())));
        }

        return new ArrayList<>(merged.values());
    }

    private List<String> mergeValues(List<String> first, List<String> second) {
        Map<String, String> values = new LinkedHashMap<>();

        Stream.concat(first.stream(), second.stream())
              .forEach(value -> values.putIfAbsent(value.toLowerCase(Locale.ROOT), value));

        return new ArrayList<>(values.values());
    }

    private void updateResult(DictionaryImportResult result, DictionaryImportRowResult rowResult) {
        switch (rowResult.status()) {
            case ADDED -> result.setAdded(result.getAdded() + 1);
            case UPDATED -> result.setUpdated(result.getUpdated() + 1);
            case UNCHANGED -> result.setUnchanged(result.getUnchanged() + 1);
        }
    }

    private String errorMessage(Exception e) {
        return e.getMessage() == null
               ? e.getClass().getSimpleName()
               : e.getMessage();
    }
}