package ru.olmi.integration.deepseek;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeepSeekResponse {

    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @Data
    @Accessors(chain = true)
    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;

        @Data
        @Accessors(chain = true)
        public static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }

}
