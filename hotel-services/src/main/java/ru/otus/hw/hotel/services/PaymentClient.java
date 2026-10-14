package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.PaymentRequest;
import ru.otus.hw.hotel.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse processPayment(PaymentRequest request);
}