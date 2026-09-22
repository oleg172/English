package ru.olmi.service.telegram.upload.callback.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DictionaryImportResult;
import ru.olmi.dto.DictionaryCsvRow;
import ru.olmi.exception.DictionaryImportException;
import ru.olmi.service.telegram.TelegramApi;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;
import ru.olmi.service.telegram.upload.DictionaryImportDialogState;
import ru.olmi.service.telegram.upload.DictionaryImportParser;
import ru.olmi.service.telegram.upload.DictionaryImportService;
import ru.olmi.service.telegram.upload.DictionaryImportView;

@Component
@RequiredArgsConstructor
public class DictionaryImportMessageHandler implements TelegramMessageHandlerDelegate {
    private final DictionaryImportDialogState dictionaryImportDialog;
    private final DictionaryImportParser parser;
    private final DictionaryImportService importService;
    private final DictionaryImportView dictionaryImportView;
    private final TelegramApi telegramApi;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return message.hasDocument() && dictionaryImportDialog.isWaitingForFile(user);
    }

    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        String fileName = message.getDocument().getFileName();
        if (fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            sender.accept(dictionaryImportView.invalidFileType(message.getChatId()));
            return;
        }

        try (InputStream inputStream = telegramApi.downloadFile(message.getDocument().getFileId())) {
            List<DictionaryCsvRow> rows = parser.parse(inputStream);
            DictionaryImportResult result = importService.importWords(user, rows);

            dictionaryImportDialog.finish(user);

            sender.accept(dictionaryImportView.result(message.getChatId(), result));
        } catch (DictionaryImportException e) {
            sender.accept(dictionaryImportView.error(message.getChatId(), e.getMessage()));
        } catch (IOException e) {
            sender.accept(dictionaryImportView.error(message.getChatId(), "Не удалось прочитать файл."));
        }
    }
}