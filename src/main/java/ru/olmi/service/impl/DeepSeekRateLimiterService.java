package ru.olmi.service.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.olmi.config.AppConfiguration;
import ru.olmi.service.RateLimiterService;

@Service
@AllArgsConstructor
@Slf4j
public class DeepSeekRateLimiterService implements RateLimiterService {

    private final AppConfiguration configuration;

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());

    @Override
    public synchronized void checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        long timeSinceReset = currentTime - lastResetTime.get();

        // Сбрасываем счетчик каждую минуту
        if (timeSinceReset > 60000) {
            requestCount.set(0);
            lastResetTime.set(currentTime);
        }

        int currentCount = requestCount.incrementAndGet();
        if (currentCount > configuration.getIntegrations().getDeepseek().getRateLimit().getRequestsPerMinute()) {
            long waitTime = 60000 - timeSinceReset;
            log.warn("Rate limit exceeded. Waiting {} ms", waitTime);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for rate limit", e);
            }
            // После ожидания сбрасываем счетчик
            requestCount.set(1);
            lastResetTime.set(System.currentTimeMillis());
        }
    }
}
