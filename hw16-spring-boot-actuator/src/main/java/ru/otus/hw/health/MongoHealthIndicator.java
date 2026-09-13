package ru.otus.hw.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    @Override
    public Health health() {
        try {
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            return Health.up()
                    .withDetail("mongo", "available")
                    .withDetail("database", mongoTemplate.getDb().getName())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("mongo", "unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}