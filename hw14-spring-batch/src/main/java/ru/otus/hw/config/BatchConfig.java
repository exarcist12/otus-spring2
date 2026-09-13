package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.document.AuthorDocument;
import ru.otus.hw.document.BookDocument;
import ru.otus.hw.document.CommentDocument;
import ru.otus.hw.document.GenreDocument;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JdbcCommentRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public JpaCursorItemReader<Author> authorReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Author>()
                .name("authorReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Author a")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Genre> genreReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Genre>()
                .name("genreReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT g FROM Genre g")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Book> bookReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genres")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Comment> commentReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Comment>()
                .name("commentReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM Comment c")
                .build();
    }

    @Bean
    public ItemProcessor<Author, AuthorDocument> authorProcessor() {
        return author -> new AuthorDocument(author.getFullName());
    }

    @Bean
    public ItemProcessor<Genre, GenreDocument> genreProcessor() {
        return genre -> new GenreDocument(genre.getName());
    }


    @Bean
    public ItemProcessor<Book, BookDocument> bookProcessor() {
        return book -> {
            BookDocument doc = new BookDocument();
            doc.setTitle(book.getTitle());
            doc.setAuthorId(String.valueOf(book.getAuthor().getId()));
            doc.setGenreIds(book.getGenres().stream()
                    .map(g -> String.valueOf(g.getId()))
                    .toList());
            return doc;
        };
    }

    @Bean
    public ItemProcessor<Comment, CommentDocument> commentProcessor() {
        return comment -> new CommentDocument(comment.getText());
    }

    @Bean
    public MongoItemWriter<AuthorDocument> authorWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<AuthorDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("authors");
        return writer;
    }

    @Bean
    public MongoItemWriter<GenreDocument> genreWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<GenreDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("genres");
        return writer;
    }

    @Bean
    public MongoItemWriter<BookDocument> bookWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<BookDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("books");
        return writer;
    }

    @Bean
    public MongoItemWriter<CommentDocument> commentWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<CommentDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("comments");
        return writer;
    }

    @Bean
    public Step migrateAuthorsStep(ItemReader<Author> reader,
                                   ItemProcessor<Author, AuthorDocument> processor,
                                   MongoItemWriter<AuthorDocument> writer) {
        return new StepBuilder("migrateAuthorsStep", jobRepository)
                .<Author, AuthorDocument>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step migrateGenresStep(ItemReader<Genre> reader,
                                  ItemProcessor<Genre, GenreDocument> processor,
                                  MongoItemWriter<GenreDocument> writer) {
        return new StepBuilder("migrateGenresStep", jobRepository)
                .<Genre, GenreDocument>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean
    public Step migrateBooksStep(ItemReader<Book> reader,
                                 ItemProcessor<Book, BookDocument> processor,
                                 MongoItemWriter<BookDocument> writer) {
        return new StepBuilder("migrateBooksStep", jobRepository)
                .<Book, BookDocument>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step migrateCommentsStep(ItemReader<Comment> reader,
                                    ItemProcessor<Comment, CommentDocument> processor,
                                    MongoItemWriter<CommentDocument> writer) {
        return new StepBuilder("migrateCommentsStep", jobRepository)
                .<Comment, CommentDocument>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Flow authorsFlow(Step migrateAuthorsStep) {
        return new FlowBuilder<Flow>("authorsFlow")
                .start(migrateAuthorsStep)
                .build();
    }

    @Bean
    public Flow genresFlow(Step migrateGenresStep) {
        return new FlowBuilder<Flow>("genresFlow")
                .start(migrateGenresStep)
                .build();
    }

    @Bean
    public Job migrateLibraryJob(Flow authorsFlow,
                                 Flow genresFlow,
                                 Step migrateBooksStep,
                                 Step migrateCommentsStep) {
        return new JobBuilder("migrateLibraryJob", jobRepository)
                .start(new FlowBuilder<Flow>("parallelFlow")
                        .split(new SimpleAsyncTaskExecutor())
                        .add(authorsFlow, genresFlow)
                        .build())
                .next(migrateBooksStep)
                .next(migrateCommentsStep)
                .end()
                .build();
    }
}