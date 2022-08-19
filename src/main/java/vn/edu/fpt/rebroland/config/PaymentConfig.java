package vn.edu.fpt.rebroland.config;


import lombok.AllArgsConstructor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;


@AllArgsConstructor
public class PaymentConfig {
    public static final String IPDEFAULT = "0:0:0:0:0:0:0:1";
    //    public static final String IPDEFAULT = "192.168.100.6";
    public static final String CURRCODE = "VND";
    public static final String VERSIONVNPAY = "2.1.0";
    //   public static final String COMMAND = "2.0.0";
    public static final String COMMAND = "pay";
    public static final String TMNCODE = "98QRZWNC";
    public static final String CHECKSUM = "QQIFCJSCRTLCMEWEGVOYCPZLJQUCUUSN";
    //thanh toan xong redirect ve trang nay
//    public static final String RETURNURL = "https://rebroland.vercel.app/thanh-toan-thanh-cong";
    public static final String RETURNURL = "https://api.rebroland.me/api/payment/recharge";
    public static final String VNPAYURL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String LOCALEDEFAULT = "vn";

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

}
