package ru.olmi.service.telegram.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.olmi.dto.DictionaryCsvRow;
import ru.olmi.exception.DictionaryImportException;

@Component
public class DictionaryImportParser {
    private static final String HEADER_WORD = "word";
    private static final String HEADER_TRANSLATIONS = "translations";
    private static final String HEADER_TOPICS = "topics";

    public List<DictionaryCsvRow> parse(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String header = reader.readLine();
            if (header == null) {
                return List.of();
            }
            validateHeader(header);

            List<DictionaryCsvRow> result = new ArrayList<>();

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                result.add(parseRow(line, lineNumber));
            }

            return result;
        } catch (DictionaryImportException e) {
            throw e;
        } catch (IOException e) {
            throw new DictionaryImportException("Failed to read import file", e);
        }
    }

    private DictionaryCsvRow parseRow(String line, int lineNumber) {
        List<String> columns = parseColumns(line);

        if (columns.size() != 3) {
            throw new DictionaryImportException("Invalid number of columns at line " + lineNumber);
        }

        String word = columns.get(0).trim();

        if (word.isEmpty()) {
            throw new DictionaryImportException("Word is empty at line " + lineNumber);
        }

        return new DictionaryCsvRow(word, parseValues(columns.get(1)), parseValues(columns.get(2))
        );
    }

    private List<String> parseColumns(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);

            if (current == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                    continue;
                }

                quoted = !quoted;
                continue;
            }

            if (current == ',' && !quoted) {
                columns.add(value.toString());
                value.setLength(0);
                continue;
            }

            value.append(current);
        }

        if (quoted) {
            throw new DictionaryImportException("Unclosed quoted value");
        }

        columns.add(value.toString());

        return columns;
    }

    private List<String> parseValues(String value) {
        if (value.isBlank()) {
            return List.of();
        }

        Map<String, String> values = new LinkedHashMap<>();

        Arrays.stream(value.split(";"))
              .map(String::trim)
              .filter(item -> !item.isEmpty())
              .forEach(item -> values.putIfAbsent(item.toLowerCase(Locale.ROOT), item));

        return new ArrayList<>(values.values());
    }

    private void validateHeader(String header) {
        List<String> columns = parseColumns(header);

        List<String> expected = List.of(
                HEADER_WORD,
                HEADER_TRANSLATIONS,
                HEADER_TOPICS
        );

        if (!expected.equals(columns.stream().map(String::trim).toList())) {
            throw new DictionaryImportException("Invalid CSV header. Expected: word,translations,topics");
        }
    }
}
