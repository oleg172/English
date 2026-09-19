package ru.olmi.service.telegram.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.dto.DictionaryImportResult;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class DictionaryImportView {

    private final TelegramMessage telegramMessage;
    private final DictionaryImportKeyboard dictionaryImportKeyboard;

    public SendMessage enterFile(Long chatId) {
        return telegramMessage.text(chatId,
                """
                        📥 Загрузка слов

                        Отправьте CSV-файл со словами.

                        Формат:
                        word,translations,topics

                        Пример:
                        reliable,"надёжный","Work;Character and personality"
                        """
        );
    }

    public SendMessage result(Long chatId, DictionaryImportResult result) {
        return telegramMessage.withKeyboard(chatId, formatResult(result), dictionaryImportKeyboard.result());
    }

    public SendMessage error(Long chatId, String message) {
        return telegramMessage.text(
                chatId,
                """
                        ❌ Не удалось загрузить файл.

                        %s

                        Отправьте исправленный CSV-файл.
                        """.formatted(message)
        );
    }

    public SendMessage invalidFileType(Long chatId) {
        return telegramMessage.text(
                chatId,
                """
                        ❌ Неверный формат файла.
        
                        Отправьте CSV-файл.
                        """
        );
    }

    private String formatResult(DictionaryImportResult result) {
        StringBuilder message = new StringBuilder();

        message.append("📥 Импорт завершён\n\n")
               .append("Обработано: ").append(result.getProcessed()).append('\n')
               .append("Добавлено: ").append(result.getAdded()).append('\n')
               .append("Обновлено: ").append(result.getUpdated()).append('\n')
               .append("Без изменений: ").append(result.getUnchanged()).append('\n')
               .append("Ошибки: ").append(result.getErrors());

        if (!result.getErrorMessages().isEmpty()) {
            message.append("\n\nОшибки:\n");

            result.getErrorMessages()
                  .forEach(error -> message.append("• ")
                                           .append(error)
                                           .append('\n')
                  );
        }

        return message.toString();
    }
}
