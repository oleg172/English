package ru.olmi.service.telegram.export;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.olmi.dto.DictionaryCsvRow;

@Component
public class DictionaryExportCsvWriter {

    private static final String HEADER = "word,translations,topics";

    public String write(List<DictionaryCsvRow> rows) {
        StringBuilder csv = new StringBuilder();

        csv.append(HEADER).append('\n');

        for (DictionaryCsvRow row : rows) {
            csv.append(value(row.word()))
               .append(',')
               .append(value(String.join(";", row.translations())))
               .append(',')
               .append(value(String.join(";", row.topics())))
               .append('\n');
        }

        return csv.toString();
    }

    private String value(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
