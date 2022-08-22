package vn.edu.fpt.rebroland.payload;

import lombok.Data;

@Data
public class BrokerPriceDTO {
    private long brokerPrice;
    private int oneMonthDiscount;
    private int threeMonthsDiscount;
    private int sixMonthsDiscount;
    private int twelveMonthsDiscount;
}
