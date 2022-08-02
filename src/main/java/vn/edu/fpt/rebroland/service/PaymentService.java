package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.PaymentDTO;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO paymentDTO);
}
