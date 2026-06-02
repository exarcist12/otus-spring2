package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvQuestionDaoTest {

    @Test
    @DisplayName("Проверка успешного парсинга вопросов из CSV")
    void checkParsing() {

        TestFileNameProvider provider = () -> "test-questions.csv";
        CsvQuestionDao dao = new CsvQuestionDao(provider);
        List<Question> questions = dao.findAll();

        assertNotNull(questions, "Список вопросов не должен быть null");
        assertEquals(2, questions.size(), "Должно быть распарсено 2 вопроса");

        Question q1 = questions.get(0);
        assertEquals("What is 2+2?", q1.text());
        assertEquals(3, q1.answers().size());
        assertFalse(q1.answers().get(0).isCorrect());
        assertTrue(q1.answers().get(1).isCorrect());
        assertFalse(q1.answers().get(2).isCorrect());

        Question q2 = questions.get(1);
        assertEquals("Java is", q2.text());
        assertEquals(2, q2.answers().size());
        assertTrue(q2.answers().get(0).isCorrect());
    }

    @Test
    @DisplayName("Проверка отображения ошибки")
    void checkException() {

        TestFileNameProvider badProvider = () -> "test-questions2.csv";
        CsvQuestionDao dao = new CsvQuestionDao(badProvider);
        assertThrows(QuestionReadException.class, () -> {
            dao.findAll();
        });
    }
}