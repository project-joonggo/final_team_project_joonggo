package com.project.joonggo.service;

import com.project.joonggo.domain.Payment;

import java.util.List;

public interface PaymentService {
    boolean savePaymentInfo(String impUid ,String merchantUid, int amount, Long boardId, String productName);


    void updatePaymentStatus(String impUid, int paidAmount);

    List<Payment> getPaymentHistory(Long userNum);
}
