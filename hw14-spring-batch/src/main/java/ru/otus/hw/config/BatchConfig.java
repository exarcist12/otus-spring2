package ru.otus.hw.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.document.AuthorDocument;
import ru.otus.hw.document.BookDocument;
import ru.otus.hw.document.CommentDocument;
import ru.otus.hw.document.GenreDocument;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JdbcCommentRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcCommentRepository commentRepository;

    @Bean
    public JpaCursorItemReader<Book> bookReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genres")
                .build();
    }

    @Bean
    public ItemProcessor<Book, BookDocument> bookProcessor() {
        return book -> {
            // Получаем автора
            AuthorDocument authorDoc = new AuthorDocument(book.getAuthor().getFullName());

            // Жанры
            List<GenreDocument> genreDocs = book.getGenres().stream()
                    .map(g -> new GenreDocument(g.getName()))
                    .toList();

            // Комментарии загружаем отдельно через репозиторий
            List<Comment> comments = commentRepository.findByBookId(book.getId());
            List<CommentDocument> commentDocs = comments.stream()
                    .map(c -> new CommentDocument(c.getText()))
                    .toList();

            return new BookDocument(
                    book.getTitle(),
                    authorDoc,
                    genreDocs,
                    commentDocs
            );
        };
    }

    @Bean
    public MongoItemWriter<BookDocument> bookWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<BookDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("books");
        return writer;
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
    public Job migrateBooksJob(Step migrateBooksStep) {
        return new JobBuilder("migrateBooksJob", jobRepository)
                .start(migrateBooksStep)
                .build();
    }
}