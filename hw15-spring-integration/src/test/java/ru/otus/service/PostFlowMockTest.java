package ru.otus.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.domain.InternationalLetter;
import ru.otus.domain.Letter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostFlowMockTest {

    @MockitoBean
    private LetterGeneratorService letterGeneratorService;
    @Autowired
    private PostGateway postGateway;

    @Test
    void shouldProcessLetterAndReturnInternationalLetter() {

        Letter letter = new Letter("Привет!", "Germany");

        InternationalLetter result = postGateway.send(letter);

        assertThat(result).isNotNull();
        assertThat(result.text()).contains("Привет!");
        assertThat(result.text()).contains("Germany");
    }
}