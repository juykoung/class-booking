package com.classbooking.payment.dto;


public record PaymentConfirmedEvent(
        Long paymentId,
        Long enrollmentId
) {
}
