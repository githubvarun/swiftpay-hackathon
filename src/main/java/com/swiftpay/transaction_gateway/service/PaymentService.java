package com.swiftpay.transaction_gateway.service;

import com.swiftpay.transaction_gateway.dto.PaymentRequest;

public interface PaymentService {

    void createPayment(PaymentRequest request);
}