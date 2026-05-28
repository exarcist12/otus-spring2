package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String fileName = fileNameProvider.getTestFileName();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            if (inputStream == null) {
                throw new QuestionReadException("File not found: " + fileName);
            }
            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .build();

            List<QuestionDto> questionDtos = csvToBean.parse();
            return questionDtos.stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new QuestionReadException("Error reading questions", e);
        }

    }
}
