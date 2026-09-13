package ru.otus;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.service.LetterGeneratorService;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final LetterGeneratorService letterGeneratorService;

    @Override
    public void run(String... args) {
        letterGeneratorService.startGenerateLettersLoop();
    }
}