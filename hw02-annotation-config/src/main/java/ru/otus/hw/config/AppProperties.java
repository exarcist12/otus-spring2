package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("application.properties")
@Component
@Data
public class AppProperties implements TestConfig, TestFileNameProvider {

    @Value("${test.rightAnswersCountToPass}")
    // внедрить свойство из application.properties
    private int rightAnswersCountToPass;

    @Value("${test.fileName}")
    // внедрить свойство из application.properties
    private String testFileName;
}
