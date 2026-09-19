package ru.olmi.service.telegram.help;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class HelpView {

    private final TelegramMessage telegramMessage;

    public SendMessage show(Long chatId) {
        return telegramMessage.withKeyboard(
                chatId,
                """
                        ❓ Помощь

                        Этот бот помогает переводить слова c английского на русский и вести свой личный словарь.

                        🔎 Найти перевод

                        Введите слово на английском или русском языке.

                        • Для английского слова бот покажет перевод, транскрипцию и примеры использования.
                        • Если ввести русское слово, бот попробует найти соответствующее английское слово.
                        • Если найдено несколько вариантов, бот предложит выбрать нужный.

                        После просмотра перевода слово можно добавить в свой словарь.

                        📚 Мои слова

                        Здесь находятся слова, которые вы добавили в свой словарь.

                        Можно:
                        • просматривать сохранённые слова;
                        • искать слово;
                        • открывать карточку слова;
                        • просматривать свои переводы и топики.

                        ✏️ Редактировать слово

                        Позволяет изменить информацию о сохранённом слове.

                        В карточке слова можно управлять своими переводами и топиками.

                        🏷 Топики

                        Топики помогают организовать слова по темам.

                        Например:
                        • Работа
                        • Путешествия
                        • Программирование
                        • Английский для собеседований

                        Одно слово можно добавить в несколько топиков.

                        💡 Совет

                        Если вы не знаете английское слово, не обязательно сначала искать его где-то ещё — попробуйте ввести русское слово в разделе «Найти перевод».
                        """,
                InlineKeyboardMarkup.builder()
                                    .keyboard(List.of(
                                            List.of(InlineKeyboardButton.builder()
                                                                        .text("↩️ Главное меню")
                                                                        .callbackData(HelpCallback.MAIN_MENU)
                                                                        .build()
                                            )
                                    ))
                                    .build()
        );
    }
}
