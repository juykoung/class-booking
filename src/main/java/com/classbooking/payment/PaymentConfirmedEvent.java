package com.classbooking.payment;


public record PaymentConfirmedEvent(
        Long paymentId,
        Long enrollmentId
) {
}
