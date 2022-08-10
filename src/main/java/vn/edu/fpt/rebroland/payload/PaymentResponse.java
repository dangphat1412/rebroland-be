package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class PaymentResponse {
    private List<TransactionDTO> payments;
    private int pageNo;
    private int totalPages;
    private Long totalResult;
}
