package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.domain.InternationalLetter;
import ru.otus.domain.Letter;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@Slf4j
@RequiredArgsConstructor
public class LetterGeneratorServiceImpl implements LetterGeneratorService {

    private static final List<Letter> LETTERS = List.of(
            new Letter("Hallo aus Deutschland!", "Germany"),
            new Letter("Salut de France", "France"),
            new Letter("Ciao dall'Italia", "Italy"),
            new Letter("Hola desde España", "Spain"),
            new Letter("Hello from the USA", "USA"),
            new Letter("Hallo aus Deutschland!", "Germany"),
            new Letter("Salut de France", "France"),
            new Letter("Ciao dall'Italia", "Italy"),
            new Letter("Hola desde España", "Spain"),
            new Letter("Hello from the US", "USA")
    );

    private final PostGateway postGateway;

    @Override
    public void startGenerateLettersLoop() {
        ForkJoinPool pool = ForkJoinPool.commonPool();

        for (int i = 0; i < LETTERS.size(); i++) {
            int num = i + 1;
            Letter letter = LETTERS.get(i);

            pool.execute(() -> {
                log.info(">>> {}, New letter: text='{}', country='{}'",
                        num, letter.text(), letter.country());

                InternationalLetter result = postGateway.send(letter);

                log.info(">>> {}, Ready letter: '{}'", num, result.text());
            });

            delay();
        }
    }


    private void delay() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}