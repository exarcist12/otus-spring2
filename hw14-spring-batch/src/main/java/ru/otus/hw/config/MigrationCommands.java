package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class MigrationCommands {

    private final JobLauncher jobLauncher;

    private final Job migrateBooksJob;

    @ShellMethod(key = "migrate", value = "Запустить миграцию книг из H2 в MongoDB")
    public String runMigration() {
        try {
            System.out.println("Запуск миграции");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(migrateBooksJob, jobParameters);

            return "Миграция завершена со статусом: " + execution.getStatus();
        } catch (Exception e) {
            return "Ошибка при миграции: " + e.getMessage();
        }
    }
}