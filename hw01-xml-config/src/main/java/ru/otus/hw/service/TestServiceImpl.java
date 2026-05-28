package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> allQuestion = questionDao.findAll();
        for (int i = 0; i < allQuestion.size(); i++) {
            if (allQuestion.get(i).text().startsWith("# Добавить сюда своих вопросов. Эту строку надо пропустить")) {
                continue;
            }
            ioService.printLine(allQuestion.get(i).text());
            List<Answer> answers = allQuestion.get(i).answers();
            for (int j = 0; j < answers.size(); j++) {
                Answer answer = answers.get(j);
                ioService.printFormattedLine("   %d) %s", j + 1, answer.text());
            }
            ioService.printLine("");
        }
    }
}