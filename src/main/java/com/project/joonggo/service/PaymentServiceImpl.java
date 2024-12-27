package com.project.joonggo.service;


import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import com.project.joonggo.domain.Payment;
import com.project.joonggo.domain.PaymentDTO;
import com.project.joonggo.repository.BoardMapper;
import com.project.joonggo.repository.FileMapper;
import com.project.joonggo.repository.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final FileMapper fileMapper;
    private final BoardMapper boardMapper;

    @Override
    public boolean savePaymentInfo(String impUid,String merchantUid, int amount, Long boardId, String productName, Long userNum) {

        Payment payment = new Payment();
        payment.setImpUid(impUid);
        payment.setMerchantUid(merchantUid);
        payment.setAmount(amount);
        payment.setBoardId(boardId);
        payment.setProductName(productName);
        payment.setUserNum(userNum);
        payment.setPaymentStatus("SUCCESS");

        paymentMapper.insertPayment(payment);
        return true;
    }

    @Override
    public void updatePaymentStatus(String impUid, int paidAmount) {
        paymentMapper.updatePaymentStatus(impUid, paidAmount);
    }

    @Override
    public List<PaymentDTO> getPaymentHistoryWithImages(Long userNum) {
        List<Payment> payments = paymentMapper.getPaymentHistory(userNum);
        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : payments) {
            Long boardId = payment.getBoardId();
            List<FileVO> images = fileMapper.getFileList(boardId);
            log.info(">>>> images >> >{}" , images);

            String fileUrl = null;
            if (!images.isEmpty()) {
                fileUrl = images.get(0).getFileUrl();  // 첫 번째 이미지 URL만 사용
            }

            String category = boardMapper.getCategory(boardId);

            log.info(">>> category >> {}", category);

            PaymentDTO paymentDTO = new PaymentDTO(payment, fileUrl, category);
            paymentDTOs.add(paymentDTO);
        }

        return paymentDTOs;
    }
}
