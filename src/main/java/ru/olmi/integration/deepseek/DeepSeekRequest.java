package ru.olmi.integration.deepseek;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeepSeekRequest {

    private String model;
    private List<Message> messages;
    private Integer max_tokens;
    private Double temperature;

    @Data
    @Accessors(chain = true)
    public static class Message {
        private String role;
        private String content;
    }
}
