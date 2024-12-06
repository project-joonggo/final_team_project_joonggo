package com.project.joonggo.service;

public interface PaymentService {
    boolean savePaymentInfo(String impUid ,String merchantUid, int amount, Long boardId, String productName);



}
