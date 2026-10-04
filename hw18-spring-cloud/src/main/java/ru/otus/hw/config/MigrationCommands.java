package ru.otus.hw.config;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class MigrationCommands {

    private final JobLauncher jobLauncher;

    private final Job migrateLibraryJob;

    @CircuitBreaker(name = "mongoMigration", fallbackMethod = "migrationFallback")
    @Retry(name = "mongoMigration")
    @ShellMethod(key = "migrate", value = "Запустить миграцию книг из H2 в MongoDB")
    public String runMigration() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(migrateLibraryJob, params);

        if (execution.getStatus() == BatchStatus.FAILED) {
            throw new RuntimeException("Миграция упала: " + execution.getExitStatus());
        }

        return "Миграция завершена: " + execution.getStatus();
    }

    private String migrationFallback(Exception e) {
        log.warn("MongoDB migration failed: {}", e.getMessage());
        return "Миграция временно недоступна. MongoDB не отвечает. Попробуйте позже.";
    }
}