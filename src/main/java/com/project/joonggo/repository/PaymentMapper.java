package com.project.joonggo.repository;

import com.project.joonggo.domain.Payment;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payment payment);


}
