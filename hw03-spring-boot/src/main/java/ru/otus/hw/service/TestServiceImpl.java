package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).text().startsWith("# Добавить сюда своих вопросов. Эту строку надо пропустить")) {
                continue;
            }
            ioService.printLine(questions.get(i).text());
            List<Answer> answers = questions.get(i).answers();
            for (int j = 0; j < answers.size(); j++) {
                Answer answer = answers.get(j);
                ioService.printFormattedLine("   %d) %s", j + 1, answer.text());
            }
            ioService.printLine("");
            var yourAnswer = ioService.readStringWithPrompt("Please input your answer: ");

            boolean isAnswerValid = answers.get(Integer.parseInt(yourAnswer) - 1).isCorrect();
            testResult.applyAnswer(questions.get(i), isAnswerValid);
        }
        return testResult;
    }
}