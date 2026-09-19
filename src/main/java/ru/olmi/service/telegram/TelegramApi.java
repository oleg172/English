package ru.olmi.service.telegram;

import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olmi.config.AppConfiguration;
import ru.olmi.exception.DictionaryImportException;

@Component
public class TelegramApi {

    private final DefaultAbsSender sender;

    public TelegramApi(AppConfiguration properties) {
        this.sender = new DefaultAbsSender(
                new DefaultBotOptions(),
                properties.getBot().getToken()
        ) {
        };
    }

    public InputStream downloadFile(String fileId) {
        try {
            File file = sender.execute(
                    GetFile.builder()
                           .fileId(fileId)
                           .build()
            );

            return sender.downloadFileAsStream(file);
        } catch (TelegramApiException e) {
            throw new DictionaryImportException(
                    "Не удалось скачать файл из Telegram.",
                    e
            );
        }
    }
}
