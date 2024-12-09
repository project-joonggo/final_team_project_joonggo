package com.project.joonggo.repository;

import com.project.joonggo.domain.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payment payment);

    void updatePaymentStatus(@Param("impUid") String impUid,
                             @Param("paidAmount") int paidAmount);
}
