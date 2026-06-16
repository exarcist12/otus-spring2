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
        student = new Student("John", "Doe");
        testResult = new TestResult(student);
    }

    @Test
    void shouldShowPassedMessageWhenRightAnswersCountIsEnough() {

        int requiredPassCount = 4;
        when(testConfig.getRightAnswersCountToPass()).thenReturn(requiredPassCount);

        for (int i = 0; i < 5; i++) {
            testResult.applyAnswer(mock(Question.class), true);
        }

        resultService.showResult(testResult);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("ResultService.test.results");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.student", "John Doe");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.answered.questions.count", 5);
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.right.answers.count", 5);
        inOrder.verify(ioService).printLineLocalized("ResultService.passed.test");

        verify(ioService, never()).printLineLocalized("ResultService.fail.test");
    }

    @Test
    void shouldShowFailedMessageWhenRightAnswersCountIsNotEnough() {

        int requiredPassCount = 4;
        when(testConfig.getRightAnswersCountToPass()).thenReturn(requiredPassCount);

        for (int i = 0; i < 3; i++) {
            testResult.applyAnswer(mock(Question.class), true);
        }
        for (int i = 0; i < 2; i++) {
            testResult.applyAnswer(mock(Question.class), false);
        }

        resultService.showResult(testResult);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("ResultService.test.results");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.student", "John Doe");
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.answered.questions.count", 5);
        inOrder.verify(ioService).printFormattedLineLocalized("ResultService.right.answers.count", 3);
        inOrder.verify(ioService).printLineLocalized("ResultService.fail.test");

        verify(ioService, never()).printLineLocalized("ResultService.passed.test");
    }

    @Test
    void shouldShowPassedMessageWhenExactlyAtThreshold() {

        int requiredPassCount = 4;
        when(testConfig.getRightAnswersCountToPass()).thenReturn(requiredPassCount);

        for (int i = 0; i < 4; i++) {
            testResult.applyAnswer(mock(Question.class), true);
        }

        resultService.showResult(testResult);

        verify(ioService).printLineLocalized("ResultService.passed.test");
        verify(ioService, never()).printLineLocalized("ResultService.fail.test");
    }

    @Test
    void shouldShowFailedMessageWhenZeroRightAnswers() {

        int requiredPassCount = 1;
        when(testConfig.getRightAnswersCountToPass()).thenReturn(requiredPassCount);

        for (int i = 0; i < 3; i++) {
            testResult.applyAnswer(mock(Question.class), false);
        }

        resultService.showResult(testResult);

        verify(ioService).printFormattedLineLocalized("ResultService.answered.questions.count", 3);
        verify(ioService).printFormattedLineLocalized("ResultService.right.answers.count", 0);
        verify(ioService).printLineLocalized("ResultService.fail.test");
        verify(ioService, never()).printLineLocalized("ResultService.passed.test");
    }

    @Test
    void shouldShowCorrectStudentFullName() {

        when(testConfig.getRightAnswersCountToPass()).thenReturn(1);

        resultService.showResult(testResult);

        verify(ioService).printFormattedLineLocalized("ResultService.student", "John Doe");
    }

    @Test
    void shouldPrintAllRequiredLinesInCorrectOrder() {

        when(testConfig.getRightAnswersCountToPass()).thenReturn(10);
        testResult.applyAnswer(mock(Question.class), true);

        resultService.showResult(testResult);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("ResultService.test.results");
        inOrder.verify(ioService).printFormattedLineLocalized(eq("ResultService.student"), any(String.class));
        inOrder.verify(ioService).printFormattedLineLocalized(eq("ResultService.answered.questions.count"), any(Integer.class));
        inOrder.verify(ioService).printFormattedLineLocalized(eq("ResultService.right.answers.count"), any(Integer.class));
        inOrder.verify(ioService).printLineLocalized(anyString());

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(2)).printLineLocalized(anyString());
        verify(ioService, times(1)).printFormattedLineLocalized(eq("ResultService.student"), any(String.class));
        verify(ioService, times(1)).printFormattedLineLocalized(eq("ResultService.answered.questions.count"), any(Integer.class));
        verify(ioService, times(1)).printFormattedLineLocalized(eq("ResultService.right.answers.count"), any(Integer.class));
    }
}