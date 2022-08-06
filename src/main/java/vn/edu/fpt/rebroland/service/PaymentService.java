package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    List<PaymentDTO> getAllPayments();
}
