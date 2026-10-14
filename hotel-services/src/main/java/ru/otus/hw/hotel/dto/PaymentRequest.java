package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    private Long bookingId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String currency = "RUB";

    private String cardNumber;
}