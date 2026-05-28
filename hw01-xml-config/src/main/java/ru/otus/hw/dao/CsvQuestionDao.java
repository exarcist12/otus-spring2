package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try {
            String fileName = fileNameProvider.getTestFileName();
            var inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new QuestionReadException("File not found: " + fileName);
            }
            List<QuestionDto> questionDtos = new CsvToBeanBuilder<QuestionDto>(
                    new InputStreamReader(inputStream))
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .build()
                    .parse();
            return questionDtos.stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new QuestionReadException("Error reading questions", e);
        }
    }
}