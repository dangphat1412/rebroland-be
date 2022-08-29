package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.TransactionDTO;
import vn.edu.fpt.rebroland.payload.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    PaymentResponse getAllPayments(int pageNumber, int pageSize, String keyword, String sortValue);

    Map<String, Long> getTotalMoney();
}
