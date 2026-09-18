package ru.olmi.service.telegram.dictionary.topic.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;

@Component
public class UserDictionaryTopicDialogState {

    private final Map<Long, TopicContext> contexts = new ConcurrentHashMap<>();

    public void start(TelegramUser user) {
        contexts.put(user.getId(), new TopicContext(null, 0, 0));
    }

    public int getTopicPage(TelegramUser user) {
        return getContext(user).topicPage();
    }

    public void setTopicPage(TelegramUser user, int page) {
        TopicContext context = getContext(user);

        contexts.put(user.getId(), new TopicContext(context.topicId(), page, context.wordPage()));
    }

    public Long getTopicId(TelegramUser user) {
        return getContext(user).topicId();
    }

    public void setTopic(TelegramUser user, Long topicId) {
        TopicContext context = getContext(user);

        contexts.put(user.getId(), new TopicContext(topicId, context.topicPage(), 0));
    }

    public int getWordPage(TelegramUser user) {
        return getContext(user).wordPage();
    }

    public void setWordPage(TelegramUser user, int page) {
        TopicContext context = getContext(user);

        contexts.put(user.getId(), new TopicContext(context.topicId(), context.topicPage(), page));
    }

    public void finish(TelegramUser user) {
        contexts.remove(user.getId());
    }

    private TopicContext getContext(TelegramUser user) {
        return contexts.getOrDefault(user.getId(), new TopicContext(null, 0, 0));
    }

    private record TopicContext(
            Long topicId,
            int topicPage,
            int wordPage
    ) {
    }
}
