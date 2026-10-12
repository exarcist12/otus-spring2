package ru.otus.hw.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.payment.dto.PaymentRequest;
import ru.otus.hw.payment.dto.PaymentResponse;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PaymentProcessor {

    public PaymentResponse process(PaymentRequest request) {
        int dice = ThreadLocalRandom.current().nextInt(100);
        log.info("Payment attempt for booking={}, amount={}, dice={}",
                request.getBookingId(), request.getAmount(), dice);

        if (dice < 25) {
            log.warn("Simulating internal error (500) for booking={}", request.getBookingId());
            throw new RuntimeException("Payment gateway internal error");
        }

        if (dice < 50) {
            log.warn("Simulating bad request (400) for booking={}", request.getBookingId());
            throw new IllegalArgumentException("Invalid card number");
        }

        boolean success = ThreadLocalRandom.current().nextBoolean();

        if (success) {
            return new PaymentResponse(
                    UUID.randomUUID().toString(),
                    "SUCCESS",
                    "Payment processed successfully"
            );
        } else {
            return new PaymentResponse(
                    UUID.randomUUID().toString(),
                    "FAILED",
                    "Insufficient funds"
            );
        }
    }
}