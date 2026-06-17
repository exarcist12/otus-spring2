package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.ResultServiceImpl;

import java.util.List;

import static org.mockito.Mockito.*;


@SpringBootTest
class ResultServiceImplTest {

    @Autowired
    private ResultServiceImpl resultService;

    @MockitoBean
    private AppProperties appProperties;

    @MockitoBean
    private LocalizedIOService ioService;

    private Student student;
    private TestResult testResult;

    @BeforeEach
    void setUp() {
        student = new Student("Test", "Testov");
        testResult = new TestResult(student);
    }

    @Test
    void shouldShowPassedMessageWhenRightAnswersCountIsEnough() {
        when(appProperties.getRightAnswersCountToPass()).thenReturn(4);

        for (int i = 0; i < 5; i++) {
            testResult.applyAnswer(new Question("Q" + i, List.of()), true);
        }

        resultService.showResult(testResult);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("ResultService.test.results");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.student", "Test Testov");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.answered.questions.count", 5);
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.right.answers.count", 5);
        inOrder.verify(ioService).printLineLocalized("ResultService.passed.test");

        verify(ioService, never()).printLineLocalized("ResultService.fail.test");
    }

}