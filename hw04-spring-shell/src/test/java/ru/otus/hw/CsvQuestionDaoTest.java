package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CsvQuestionDaoTest {

    @Autowired
    private CsvQuestionDao questionDao;

    @Test
    void shouldLoadQuestionsFromCsvCorrectly() {

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

}