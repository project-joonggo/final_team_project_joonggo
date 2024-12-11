package com.project.joonggo.service;

import com.project.joonggo.domain.Payment;
import com.project.joonggo.domain.PaymentDTO;

import java.util.List;

public interface PaymentService {
    boolean savePaymentInfo(String impUid ,String merchantUid, int amount, Long boardId, String productName, Long userNum);


    void updatePaymentStatus(String impUid, int paidAmount);

    List<PaymentDTO> getPaymentHistoryWithImages(Long userNum);
}
