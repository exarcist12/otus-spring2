package ru.otus.hw.hotel.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.otus.hw.hotel.dto.PaymentRequest;
import ru.otus.hw.hotel.dto.PaymentResponse;

@Slf4j
@Component
public class PaymentClientRest implements PaymentClient {

    private final RestClient restClient;

    public PaymentClientRest(@Value("${payment.service.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Calling payment-service: bookingId={}, amount={}",
                request.getBookingId(), request.getAmount());

        return restClient.post()
                .uri("/api/payments")
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }
}