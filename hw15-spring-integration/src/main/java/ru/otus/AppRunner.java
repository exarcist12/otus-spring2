package ru.otus;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.domain.InternationalLetter;
import ru.otus.domain.Letter;
import ru.otus.service.PostGateway;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final PostGateway postGateway;

    @Override
    public void run(String... args) {

        Letter letter = new Letter(
                "Привет из России!",
                "Germany"
        );

        InternationalLetter result = postGateway.send(letter);

        System.out.println(result);
    }
}