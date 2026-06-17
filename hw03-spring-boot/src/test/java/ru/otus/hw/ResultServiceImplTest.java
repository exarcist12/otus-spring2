package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.ResultServiceImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultServiceImplTest {

    @Mock
    private TestConfig testConfig;

    @Mock
    private LocalizedIOService ioService;

    private ResultServiceImpl resultService;

    private Student student;
    private TestResult testResult;

    @BeforeEach
    void setUp() {
        resultService = new ResultServiceImpl(testConfig, ioService);
        student = new Student("Test", "Testov");
        testResult = new TestResult(student);
    }

    @Test
    void shouldShowPassedMessageWhenRightAnswersCountIsEnough() {
        when(testConfig.getRightAnswersCountToPass()).thenReturn(4);

        for (int i = 0; i < 5; i++) {
            testResult.applyAnswer(mock(Question.class), true);
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