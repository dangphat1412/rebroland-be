package vn.edu.fpt.rebroland.config;




import lombok.AllArgsConstructor;


@AllArgsConstructor
public class PaymentConfig {
    public static final String IPDEFAULT = "0:0:0:0:0:0:0:1";
    public static final String CURRCODE = "VND";
    public static final String VERSIONVNPAY = "2.1.0";
    public static final String COMMAND = "2.0.0";
    public static final String TMNCODE = "";
    public static final String CHECKSUM = "";
    public static final String RETURNURL = "";
    public static final String VNPAYURL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String LOCALEDEFAULT = "vn";

}
