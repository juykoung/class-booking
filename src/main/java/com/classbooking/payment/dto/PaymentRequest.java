package com.classbooking.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long enrollmentId,
        BigDecimal amount
) {
}
