package com.classbooking.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        Long enrollmentId,
        BigDecimal amount
) {
}
