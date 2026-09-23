package ru.olmi.service.telegram.learning.menu;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LearningMenuService {

    public LearningMenu getMenu() {
        return new LearningMenu(
                "Выберите способ изучения:",
                List.of(new LearningMenu.Item("📚 По топику", LearningMenu.Action.TOPIC))
        );
    }
}
