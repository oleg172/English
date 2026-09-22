package ru.olmi.service.telegram.export;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.service.telegram.TelegramApi;

@Component
@RequiredArgsConstructor
public class DictionaryExportView {

    private final TelegramApi telegramApi;

    public void send(Long chatId, String csv) {
        telegramApi.sendDocument(chatId, csv.getBytes(StandardCharsets.UTF_8), "dictionary.csv");
    }
}
