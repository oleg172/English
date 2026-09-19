package ru.olmi.service.upoad;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.olmi.dto.DictionaryImportRow;
import ru.olmi.exception.DictionaryImportException;
import ru.olmi.service.telegram.upload.DictionaryImportParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DictionaryImportParserTest {

    private final DictionaryImportParser parser = new DictionaryImportParser();

    @Test
    void shouldParseRows() {
        String csv = """
                word,translations,topics
                abandon,,"Character and personality"
                ambitious,"амбициозный;целеустремлённый","Character and personality;Work"
                conscientious,,"Character and personality"
                """;

        List<DictionaryImportRow> result = parser.parse(input(csv));

        assertThat(result).hasSize(3);

        assertThat(result.get(0).word()).isEqualTo("abandon");
        assertThat(result.get(0).translations()).isEmpty();
        assertThat(result.get(0).topics()).containsExactly("Character and personality");

        assertThat(result.get(1).word())
                .isEqualTo("ambitious");
        assertThat(result.get(1).translations())
                .containsExactly("амбициозный", "целеустремлённый");
        assertThat(result.get(1).topics())
                .containsExactly("Character and personality", "Work");
    }

    @Test
    void shouldTrimValues() {
        String csv = """
                word,translations,topics
                  ambitious  ," амбициозный ; целеустремлённый "," Work ; Character "
                """;

        List<DictionaryImportRow> result = parser.parse(input(csv));

        assertThat(result.get(0).word())
                .isEqualTo("ambitious");

        assertThat(result.get(0).translations())
                .containsExactly("амбициозный", "целеустремлённый");

        assertThat(result.get(0).topics())
                .containsExactly("Work", "Character");
    }

    @Test
    void shouldRemoveDuplicateTranslationsAndTopics() {
        String csv = """
                word,translations,topics
                ambitious,"амбициозный;амбициозный","Work;Work"
                """;

        List<DictionaryImportRow> result = parser.parse(input(csv));

        assertThat(result.get(0).translations())
                .containsExactly("амбициозный");

        assertThat(result.get(0).topics())
                .containsExactly("Work");
    }

    @Test
    void shouldSkipBlankLines() {
        String csv = """
                word,translations,topics

                ambitious,"амбициозный","Work"

                
                reliable,"надёжный","Work"
                """;

        List<DictionaryImportRow> result = parser.parse(input(csv));

        assertThat(result)
                .extracting(DictionaryImportRow::word)
                .containsExactly("ambitious", "reliable");
    }

    @Test
    void shouldAllowEmptyTranslationsAndTopics() {
        String csv = """
                word,translations,topics
                ambitious,,
                reliable,"надёжный",
                conscientious,,"Character"
                """;

        List<DictionaryImportRow> result = parser.parse(input(csv));

        assertThat(result).hasSize(3);

        assertThat(result.get(0).translations()).isEmpty();
        assertThat(result.get(0).topics()).isEmpty();

        assertThat(result.get(1).translations())
                .containsExactly("надёжный");
        assertThat(result.get(1).topics()).isEmpty();

        assertThat(result.get(2).translations()).isEmpty();
        assertThat(result.get(2).topics())
                .containsExactly("Character");
    }

    @Test
    void shouldRejectInvalidHeader() {
        String csv = """
                word,translation,topics
                ambitious,"амбициозный","Work"
                """;

        assertThatThrownBy(() -> parser.parse(input(csv)))
                .isInstanceOf(DictionaryImportException.class)
                .hasMessageContaining("Invalid CSV header");
    }

    @Test
    void shouldRejectEmptyWord() {
        String csv = """
                word,translations,topics
                ,"перевод","Work"
                """;

        assertThatThrownBy(() -> parser.parse(input(csv)))
                .isInstanceOf(DictionaryImportException.class)
                .hasMessageContaining("Word is empty");
    }

    @Test
    void shouldRejectInvalidNumberOfColumns() {
        String csv = """
                word,translations,topics
                ambitious,"амбициозный"
                """;

        assertThatThrownBy(() -> parser.parse(input(csv)))
                .isInstanceOf(DictionaryImportException.class)
                .hasMessageContaining("Invalid number of columns");
    }

    @Test
    void shouldRejectUnclosedQuotes() {
        String csv = """
                word,translations,topics
                ambitious,"амбициозный,Work
                """;

        assertThatThrownBy(() -> parser.parse(input(csv)))
                .isInstanceOf(DictionaryImportException.class)
                .hasMessageContaining("Unclosed quoted value");
    }

    @Test
    void shouldReturnEmptyListForEmptyFile() {
        List<DictionaryImportRow> result =
                parser.parse(input(""));

        assertThat(result).isEmpty();
    }

    private InputStream input(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
