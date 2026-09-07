package ru.otus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.service.PostOfficeService;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> lettersChannel() {
        return MessageChannels.queue(10);
    }

//    @Bean
//    public MessageChannelSpec<?, ?> deliveredLettersChannel() {
//        return MessageChannels.publishSubscribe();
//    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100)
                .maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow postFlow(
            PostOfficeService postOfficeService
    ) {
        return IntegrationFlow
                .from(lettersChannel())
                .handle(postOfficeService, "prepareLetter")
    //            .channel(deliveredLettersChannel())
                .get();
    }
}