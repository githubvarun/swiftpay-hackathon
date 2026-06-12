package com.swiftpay.transaction_gateway.service;

import com.swiftpay.transaction_gateway.dto.PaymentEvent;
import com.swiftpay.transaction_gateway.dto.PaymentRequest;
import com.swiftpay.transaction_gateway.entity.PaymentTransaction;
import com.swiftpay.transaction_gateway.entity.TransactionStatus;
import com.swiftpay.transaction_gateway.producer.PaymentEventProducer;
import com.swiftpay.transaction_gateway.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentEventProducer producer;

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public void createPayment(PaymentRequest request) {

        PaymentTransaction transaction =
                PaymentTransaction.builder()
                        .transactionId(UUID.randomUUID())
                        .senderId(request.getSenderId())
                        .receiverId(request.getReceiverId())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .status(TransactionStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build();

        paymentTransactionRepository.save(transaction);

        PaymentEvent event =
                PaymentEvent.builder()
                        .transactionId(transaction.getTransactionId())
                        .senderId(transaction.getSenderId())
                        .receiverId(transaction.getReceiverId())
                        .amount(transaction.getAmount())
                        .currency(transaction.getCurrency())
                        .status(transaction.getStatus().name())
                        .createdAt(transaction.getCreatedAt())
                        .build();

        producer.publish(event);
    }
}