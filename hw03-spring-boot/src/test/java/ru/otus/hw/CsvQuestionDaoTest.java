package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {

    private CsvQuestionDao questionDao;
    private TestFileNameProvider fileNameProvider;

    @BeforeEach
    void setUp() {
        fileNameProvider = mock(TestFileNameProvider.class);
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    void shouldLoadQuestionsFromCsvCorrectly() {
        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");

        List<Question> questions = questionDao.findAll();

        assertThat(questions).isNotNull();
        assertThat(questions).hasSize(2);

        Question firstQuestion = questions.get(0);
        assertThat(firstQuestion.text()).isEqualTo("Сколько будет 2+2?");
        assertThat(firstQuestion.answers()).hasSize(3);
        assertThat(firstQuestion.answers().get(0).isCorrect()).isTrue();
        assertThat(firstQuestion.answers().get(1).isCorrect()).isFalse();

        Question secondQuestion = questions.get(1);
        assertThat(secondQuestion.text()).isEqualTo("Какая столица Франции?");
        assertThat(secondQuestion.answers().get(1).isCorrect()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        when(fileNameProvider.getTestFileName()).thenReturn("non-existent-file.csv");

        assertThatThrownBy(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("File not found");
    }
}