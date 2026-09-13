package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.document.AuthorDocument;
import ru.otus.hw.document.BookDocument;
import ru.otus.hw.document.CommentDocument;
import ru.otus.hw.document.GenreDocument;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false",
        "spring.sql.init.mode=always"
})
@DisplayName("Тесты Spring Batch миграции в MongoDB")
class MigrationTest2 {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job migrateLibraryJob;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(migrateLibraryJob);
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("Job миграции должен успешно выполниться и перенести данные")
    void shouldMigrateDataSuccessfully() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        assertThat(execution.getStatus().isUnsuccessful()).isFalse();
        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        assertThat(mongoTemplate.findAll(AuthorDocument.class)).isNotEmpty();
        assertThat(mongoTemplate.findAll(GenreDocument.class)).isNotEmpty();
        assertThat(mongoTemplate.findAll(BookDocument.class)).isNotEmpty();
        assertThat(mongoTemplate.findAll(CommentDocument.class)).isNotEmpty();
    }

    @Test
    @DisplayName("Должны мигрировать все авторы")
    void shouldMigrateAllAuthors() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(params);

        var authors = mongoTemplate.findAll(AuthorDocument.class);
        assertThat(authors).hasSize(3);
        assertThat(authors).extracting(AuthorDocument::getFullName)
                .containsExactlyInAnyOrder("Author_1", "Author_2", "Author_3");
    }

    @Test
    @DisplayName("Должны мигрировать все книги")
    void shouldMigrateAllBooks() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(params);

        var books = mongoTemplate.findAll(BookDocument.class);
        assertThat(books).hasSize(3);
        assertThat(books).extracting(BookDocument::getTitle)
                .containsExactlyInAnyOrder("BookTitle_1", "BookTitle_2", "BookTitle_3");
    }

    @Test
    @DisplayName("Должны мигрировать все комментарии")
    void shouldMigrateAllComments() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(params);

        var comments = mongoTemplate.findAll(CommentDocument.class);
        assertThat(comments).isNotEmpty();
    }
}