package com.project.joonggo.service;


import com.project.joonggo.domain.Payment;
import com.project.joonggo.repository.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;

    @Override
    public boolean savePaymentInfo(String merchantUid, int amount, Long boardId, String productName) {

        Payment payment = new Payment();
        payment.setMerchantUid(merchantUid);
        payment.setAmount(amount);
        payment.setBoardId(boardId);
        payment.setProductName(productName);
        payment.setPaymentStatus("SUCCESS");

        paymentMapper.insertPayment(payment);
        return true;
    }



}
