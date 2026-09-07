package ru.otus.service;

import ru.otus.domain.InternationalLetter;
import ru.otus.domain.Letter;

public interface PostOfficeService {

    InternationalLetter prepareLetter(Letter letter);
}