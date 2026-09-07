package ru.otus.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.domain.InternationalLetter;
import ru.otus.domain.Letter;

@MessagingGateway
public interface PostGateway {

    @Gateway(requestChannel = "lettersChannel")
    //@Gateway(requestChannel = "lettersChannel", replyChannel = "deliveredLettersChannel")
    InternationalLetter send(Letter letter);


}
