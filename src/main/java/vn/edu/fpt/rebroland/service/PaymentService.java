package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.PaymentDTO;
import vn.edu.fpt.rebroland.payload.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentResponse getAllPayments(int pageNumber, int pageSize, String keyword, String sortValue);

    Map<String, Long> getTotalMoney();
}
