package ru.olmi.service.upoad;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DictionaryImportResult;
import ru.olmi.dto.DictionaryCsvRow;
import ru.olmi.dto.DictionaryImportRowResult;
import ru.olmi.dto.enums.DictionaryImportRowStatus;
import ru.olmi.service.telegram.upload.DictionaryImportRowService;
import ru.olmi.service.telegram.upload.DictionaryImportService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryImportServiceTest {

    @Mock
    private DictionaryImportRowService rowService;

    @Mock
    private TelegramUser user;

    @InjectMocks
    private DictionaryImportService service;

    @Test
    void shouldAggregateImportResult() {
        List<DictionaryCsvRow> rows = List.of(
                new DictionaryCsvRow(
                        "word1",
                        List.of(),
                        List.of()
                ),
                new DictionaryCsvRow(
                        "word2",
                        List.of(),
                        List.of()
                ),
                new DictionaryCsvRow(
                        "word3",
                        List.of(),
                        List.of()
                )
        );

        when(rowService.importRow(user, rows.get(0))).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.ADDED));
        when(rowService.importRow(user, rows.get(1))).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.UPDATED));
        when(rowService.importRow(user, rows.get(2))).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.UNCHANGED));

        DictionaryImportResult result = service.importWords(user, rows);

        assertThat(result.getProcessed()).isEqualTo(3);
        assertThat(result.getAdded()).isEqualTo(1);
        assertThat(result.getUpdated()).isEqualTo(1);
        assertThat(result.getUnchanged()).isEqualTo(1);
        assertThat(result.getErrors()).isZero();
        assertThat(result.getErrorMessages()).isEmpty();

        verify(rowService).importRow(user, rows.get(0));
        verify(rowService).importRow(user, rows.get(1));
        verify(rowService).importRow(user, rows.get(2));
    }

    @Test
    void shouldContinueImportWhenRowFails() {
        DictionaryCsvRow failedRow =
                new DictionaryCsvRow(
                        "broken",
                        List.of(),
                        List.of()
                );

        DictionaryCsvRow successfulRow =
                new DictionaryCsvRow(
                        "reliable",
                        List.of(),
                        List.of()
                );

        when(rowService.importRow(user, failedRow)).thenThrow(new RuntimeException("Google translation failed"));
        when(rowService.importRow(user, successfulRow)).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.ADDED));

        DictionaryImportResult result = service.importWords(user, List.of(failedRow, successfulRow));

        assertThat(result.getProcessed()).isEqualTo(2);
        assertThat(result.getAdded()).isEqualTo(1);
        assertThat(result.getUpdated()).isZero();
        assertThat(result.getUnchanged()).isZero();
        assertThat(result.getErrors()).isEqualTo(1);
        assertThat(result.getErrorMessages()).containsExactly("Слово 'broken': Google translation failed");

        verify(rowService).importRow(user, failedRow);
        verify(rowService).importRow(user, successfulRow);
    }

    @Test
    void shouldUseExceptionClassWhenMessageIsNull() {
        DictionaryCsvRow row =
                new DictionaryCsvRow(
                        "broken",
                        List.of(),
                        List.of()
                );

        when(rowService.importRow(user, row)).thenThrow(new RuntimeException());

        DictionaryImportResult result = service.importWords(user, List.of(row));

        assertThat(result.getProcessed()).isEqualTo(1);
        assertThat(result.getErrors()).isEqualTo(1);
        assertThat(result.getErrorMessages()).containsExactly("Слово 'broken': RuntimeException");
    }

    @Test
    void shouldReturnEmptyResultForEmptyRows() {
        DictionaryImportResult result = service.importWords(user, List.of());

        assertThat(result.getProcessed()).isZero();
        assertThat(result.getAdded()).isZero();
        assertThat(result.getUpdated()).isZero();
        assertThat(result.getUnchanged()).isZero();
        assertThat(result.getErrors()).isZero();
        assertThat(result.getErrorMessages()).isEmpty();

        verifyNoInteractions(rowService);
    }

    @Test
    void shouldMergeRowsWithSameWordIgnoringCase() {
        DictionaryCsvRow first = new DictionaryCsvRow(
                "Reliable",
                List.of("надёжный"),
                List.of("Work")
        );

        DictionaryCsvRow second = new DictionaryCsvRow(
                "reliable",
                List.of("достоверный"),
                List.of("Character and personality")
        );

        when(rowService.importRow(user, new DictionaryCsvRow(
                "Reliable",
                List.of("надёжный", "достоверный"),
                List.of("Work", "Character and personality")
        ))).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.ADDED));

        DictionaryImportResult result = service.importWords(user, List.of(first, second));

        assertThat(result.getProcessed()).isEqualTo(1);
        assertThat(result.getAdded()).isEqualTo(1);

        verify(rowService).importRow(user, new DictionaryCsvRow("Reliable", List.of("надёжный", "достоверный"),
                List.of("Work", "Character and personality")));
    }

    @Test
    void shouldMergeTranslationsIgnoringCase() {
        DictionaryCsvRow first = new DictionaryCsvRow(
                "reliable",
                List.of("Надёжный", "достоверный"),
                List.of()
        );

        DictionaryCsvRow second = new DictionaryCsvRow(
                "reliable",
                List.of("надёжный", "НАДЁЖНЫЙ", "Надежный"),
                List.of()
        );

        when(rowService.importRow(eq(user), any(DictionaryCsvRow.class)
        )).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.ADDED));

        DictionaryImportResult result = service.importWords(user, List.of(first, second));

        assertThat(result.getProcessed()).isEqualTo(1);

        ArgumentCaptor<DictionaryCsvRow> captor = ArgumentCaptor.forClass(DictionaryCsvRow.class);

        verify(rowService).importRow(eq(user), captor.capture());

        assertThat(captor.getValue().translations()).containsExactly("Надёжный", "достоверный", "Надежный");
    }

    @Test
    void shouldMergeTopicsIgnoringCase() {
        DictionaryCsvRow first = new DictionaryCsvRow(
                "reliable",
                List.of(),
                List.of("Work", "Character and personality")
        );

        DictionaryCsvRow second = new DictionaryCsvRow(
                "RELIABLE",
                List.of(),
                List.of(
                        "work",
                        "character and personality",
                        "Communication"
                )
        );

        when(rowService.importRow(eq(user), any(DictionaryCsvRow.class)
        )).thenReturn(new DictionaryImportRowResult(DictionaryImportRowStatus.ADDED));

        DictionaryImportResult result = service.importWords(user, List.of(first, second));

        assertThat(result.getProcessed()).isEqualTo(1);
        ArgumentCaptor<DictionaryCsvRow> captor = ArgumentCaptor.forClass(DictionaryCsvRow.class);
        verify(rowService).importRow(eq(user), captor.capture());

        assertThat(captor.getValue().topics()).containsExactly(
                        "Work",
                        "Character and personality",
                        "Communication"
                );
    }


}
