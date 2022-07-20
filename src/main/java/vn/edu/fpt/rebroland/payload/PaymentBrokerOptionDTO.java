package vn.edu.fpt.rebroland.payload;

import lombok.Data;

@Data
public class PaymentBrokerOptionDTO {
    private int id;

    private String option;
    private int amount;
    private int discount;
}
