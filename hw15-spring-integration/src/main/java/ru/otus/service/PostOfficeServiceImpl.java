package ru.otus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.domain.InternationalLetter;
import ru.otus.domain.Letter;

@Service
@Slf4j
public class PostOfficeServiceImpl implements PostOfficeService {

    @Override
    public InternationalLetter prepareLetter(Letter letter) {

        log.info("Preparing letter for {}", letter.country());
        return new InternationalLetter(letter.text() + ":letter sent from " + letter.country());
    }
}
