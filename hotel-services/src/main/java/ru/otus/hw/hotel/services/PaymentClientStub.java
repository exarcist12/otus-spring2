package ru.otus.hw.hotel.services;

import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.PaymentRequest;
import ru.otus.hw.hotel.dto.PaymentResponse;

import java.util.UUID;

@Component
public class PaymentClientStub implements PaymentClient {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // Заглушка: всегда успех
        return new PaymentResponse(UUID.randomUUID().toString(), "SUCCESS", "OK");
    }
}