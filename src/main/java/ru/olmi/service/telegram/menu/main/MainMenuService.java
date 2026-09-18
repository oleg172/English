package ru.olmi.service.telegram.menu.main;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MainMenuService {

    public MainMenu getMenu() {
        return new MainMenu(
                "Выберите действие:",
                List.of(
                        new MainMenu.Item("🔎 Найти перевод", MainMenu.Action.TRANSLATE),
                        new MainMenu.Item("✏️ Редактировать слово", MainMenu.Action.EDIT_WORD),
                        new MainMenu.Item("📚 Мои слова", MainMenu.Action.MY_WORDS)
                )
        );
    }
}
