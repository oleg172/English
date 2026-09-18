package ru.olmi.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class SchedulerConfig {

    @Bean
    @Lazy
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "TemporaryMessageScheduler");
            t.setDaemon(true);
            return t;
        });
    }
}
