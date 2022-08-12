package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.config.PaymentConfig;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.OtpService;
import vn.edu.fpt.rebroland.service.PaymentService;
import vn.edu.fpt.rebroland.service.UserService;
import vn.edu.fpt.rebroland.service.WithdrawService;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/payment")
public class PaymentController {

    private PaymentService paymentService;
    private UserRepository userRepository;
    private ModelMapper mapper;
    private UserService userService;
    private WithdrawService withdrawService;

    public PaymentController(PaymentService paymentService, UserRepository userRepository, ModelMapper mapper, UserService userService, WithdrawService withdrawService) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userService = userService;
        this.withdrawService = withdrawService;
    }

    @Autowired
    private OtpService otpService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody TransactionDTO transactionDTO,
                                           @RequestHeader(name = "Authorization") String token) throws UnsupportedEncodingException {
//        int amount = 0;
//        if(transactionDTO.getTypeId() == 1){
//            amount = 55000 * 100;
//        }else{
//            amount = 100000 * 100;
//        }
//        User user = getUserFromToken(token);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.VERSIONVNPAY);
        vnp_Params.put("vnp_Command", PaymentConfig.COMMAND);
        vnp_Params.put("vnp_TmnCode", PaymentConfig.TMNCODE);
        vnp_Params.put("vnp_Amount", String.valueOf(transactionDTO.getAmount() * 100));
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_CurrCode", PaymentConfig.CURRCODE);
        vnp_Params.put("vnp_TxnRef", PaymentConfig.getRandomNumber(8));
        vnp_Params.put("vnp_OrderInfo", "Nap tien vao vi");
        vnp_Params.put("vnp_Locale", PaymentConfig.LOCALEDEFAULT);
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.RETURNURL + "?token=" + token);
//        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.RETURNURL);
        vnp_Params.put("vnp_IpAddr", PaymentConfig.IPDEFAULT);

        Date dt = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = formater.format(dt);
        vnp_Params.put("vnp_CreateDate", date);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.CHECKSUM, hashData.toString());
//        String vnp_SecureHash = Hashing.sha256().hashString(PaymentConfig.CHECKSUM + hashData, StandardCharsets.UTF_8).toString();
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.VNPAYURL + "?" + queryUrl;

        Map<String, Object> result = new HashMap<>();
        result.put("message", "success");
        result.put("data", paymentUrl);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @GetMapping("/payment-info")
//    public ResponseEntity<?> transactionHandle(@RequestParam(value = "vnp_Amount", required = false) String amount,
//                                               @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
//                                               @RequestParam(value = "vnp_BankTranNo", required = false) String bankTranNo,
//                                               @RequestParam(value = "vnp_CardType", required = false) String cardType,
//                                               @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
//                                               @RequestParam(value = "vnp_PayDate", required = false) String payDate,
//                                               @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
//                                               @RequestParam(value = "vnp_TmnCode", required = false) String tmnCode,
//                                               @RequestParam(value = "vnp_TransactionNo", required = false) String transactionNo,
//                                               @RequestParam(value = "vnp_TxnRef", required = false) String txnRef,
//                                               @RequestParam(value = "vnp_SecureHashType", required = false) String secureHashType,
//                                               @RequestParam(value = "vnp_SecureHash", required = false) String secureHash,
//                                               @RequestParam(value = "token") String token
//                                               ){
//        TransactionDTO newPayment = new TransactionDTO();
//        int price = Integer.parseInt(amount) / 100;
//        newPayment.setAmount(price);
//        newPayment.setDescription(orderInfo);
//        if(price == 55000){
//            newPayment.setTypeId(1);
//        }else{
//            newPayment.setTypeId(2);
//        }
//
//        //fix user id
//        User user = getUserFromToken(token);
//        newPayment.setUser(mapper.map(user, UserDTO.class));
//
//
//        TransactionDTO transactionDTO = paymentService.createTransaction(newPayment);
//        return new ResponseEntity<>(transactionDTO, HttpStatus.OK);
//    }

    @RequestMapping("/recharge")
    public void depositMoneyIntoWallet(@RequestParam(value = "vnp_Amount", required = false) String amount,
                                                    @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
                                                    @RequestParam(value = "vnp_BankTranNo", required = false) String bankTranNo,
                                                    @RequestParam(value = "vnp_CardType", required = false) String cardType,
                                                    @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
                                                    @RequestParam(value = "vnp_PayDate", required = false) String payDate,
                                                    @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
                                                    @RequestParam(value = "vnp_TmnCode", required = false) String tmnCode,
                                                    @RequestParam(value = "vnp_TransactionNo", required = false) String transactionNo,
                                                    @RequestParam(value = "vnp_TxnRef", required = false) String txnRef,
                                                    @RequestParam(value = "vnp_SecureHashType", required = false) String secureHashType,
                                                    @RequestParam(value = "vnp_SecureHash", required = false) String secureHash,
                                                    @RequestParam(value = "token") String token,
                                                    HttpServletResponse response){
        try{
            TransactionDTO transactionDTO = new TransactionDTO();
            User user = getUserFromToken(token);
            transactionDTO.setUser(mapper.map(user, UserDTO.class));
            transactionDTO.setTypeId(3);
            long money = Long.parseLong(amount) / 100;
            transactionDTO.setAmount(money);
            transactionDTO.setDescription(orderInfo);

            TransactionDTO newTransactionDTO = paymentService.createTransaction(transactionDTO);
            if(newTransactionDTO != null){
                long accountBalance = user.getAccountBalance();
                user.setAccountBalance(accountBalance + transactionDTO.getAmount());
                userRepository.save(user);
                final String linkRedirect = "https://rebroland-frontend.vercel.app/thanh-toan-thanh-cong?amount=" + money +"&orderInfo="
                        + orderInfo +"&bankCode=" + bankCode + "&cardType=" + cardType + "&payDate=" + payDate + "&transactionNo=" + transactionNo;
                response.sendRedirect(linkRedirect);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public void autoRenewPost(boolean auto){
//        if(auto){
//
//        }
//    }

    @PostMapping("/transfer/send-otp")
    public ResponseEntity<?> preTransfer(@RequestHeader(name = "Authorization") String token,
                                         @Valid @RequestBody TransferDTO registerDTO){
        User user = getUserFromToken(token);
        if(user.getPhone().equals(registerDTO.getPhone())){
            return new ResponseEntity<>("SĐT nhận tiền trùng với SĐT hiện tại !", HttpStatus.BAD_REQUEST);
        }else{
            UserDTO userDTO = userService.getUserByPhone(registerDTO.getPhone());
            if(userDTO != null){
//                User sender = userRepository.findByPhone(user.getPhone()).
//                        orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + user.getPhone()));
                long amount = registerDTO.getAmount();
                long senderAccountBalance = user.getAccountBalance();
                if(amount > senderAccountBalance){
                    return new ResponseEntity<>("Số dư không đủ để thực hiện giao dịch!", HttpStatus.BAD_REQUEST);
                }

                String otp = otpService.generateOtp(user.getPhone()) + "";
//                sendSMS(user.getPhone(), otp);
                Map<String, Object> map = new HashMap<>();
                map.put("transferData", registerDTO);
                map.put("tokenTime", otpService.EXPIRE_MINUTES);
                map.put("otp", otp);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }else {
                return new ResponseEntity<>("Số điện thoại nhận tiền không tồn tại!", HttpStatus.BAD_REQUEST);
            }

        }
    }

    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<?> processTransfer(@RequestHeader(name = "Authorization") String token,
                                                @Valid @RequestBody TransferDTO transferDTO){
        User sender = getUserFromToken(token);
        if(otpService.getOtp(transferDTO.getPhone()) == null){
            return new ResponseEntity<>("Mã OTP hết hạn!", HttpStatus.BAD_REQUEST);
        }
        if(transferDTO.getToken().isEmpty() || transferDTO.getToken() == null){
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }
        int otp = Integer.parseInt(transferDTO.getToken());

        if (otp != otpService.getOtp(transferDTO.getPhone())) {
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }else{
            UserDTO userDTO = userService.getUserByPhone(transferDTO.getPhone());
            if(userDTO != null){
                User receiver = userRepository.findByPhone(transferDTO.getPhone()).
                        orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + transferDTO.getPhone()));
                long amount = transferDTO.getAmount();

                if(amount > sender.getAccountBalance()){
                    return new ResponseEntity<>("Số dư không đủ để thực hiện giao dịch!", HttpStatus.BAD_REQUEST);
                }
                long newSenderAccountBalance = sender.getAccountBalance() - amount;
                long newReceiverAccountBalance = receiver.getAccountBalance() + amount;
                sender.setAccountBalance(newSenderAccountBalance);
                userRepository.save(sender);

                receiver.setAccountBalance(newReceiverAccountBalance);
                userRepository.save(receiver);

                TransactionDTO senderDto = new TransactionDTO();
                senderDto.setUser(mapper.map(sender, UserDTO.class));
                senderDto.setDescription(transferDTO.getContent());
                senderDto.setAmount(amount);
                senderDto.setTypeId(4);
                TransactionDTO dto = paymentService.createTransaction(senderDto);

                TransactionDTO receiverDto = new TransactionDTO();
                receiverDto.setUser(mapper.map(receiver, UserDTO.class));
                receiverDto.setDescription("Nhận tiền");
                receiverDto.setAmount(amount);
                receiverDto.setTypeId(5);
                TransactionDTO transactionDTO = paymentService.createTransaction(receiverDto);
                if(dto != null && transactionDTO != null){
                    transferDTO.setToken(null);
                    return new ResponseEntity<>(transferDTO, HttpStatus.CREATED);
                }else{
                    return new ResponseEntity<>("Chuyển tiền thất bại!", HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>("SĐT nhận tiền không tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
            }

        }

    }

    @PostMapping("/cash-out/send-otp")
    public ResponseEntity<?> preWithdraw(@RequestHeader(name = "Authorization") String token,
                                             @Valid @RequestBody WithdrawDTO withdrawDTO){
        User user = getUserFromToken(token);
        if (user != null) {
            long amount = withdrawDTO.getMoney();
            long accountBalance = user.getAccountBalance();
            if (amount > accountBalance) {
                return new ResponseEntity<>("Số dư không đủ để thực hiện giao dịch!", HttpStatus.BAD_REQUEST);
            }

            String otp = otpService.generateOtp(user.getPhone()) + "";
//                sendSMS(user.getPhone(), otp);
            Map<String, Object> map = new HashMap<>();
            map.put("cashoutData", withdrawDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES);
            map.put("otp", otp);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Số điện thoại không tồn tại!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cash-out")
    @Transactional
    public ResponseEntity<?> processWithdraw(@RequestHeader(name = "Authorization") String token,
                                             @Valid @RequestBody WithdrawDTO withdrawDTO){
        User sender = getUserFromToken(token);
        if(otpService.getOtp(sender.getPhone()) == null){
            return new ResponseEntity<>("Mã OTP hết hạn!", HttpStatus.BAD_REQUEST);
        }
        if(withdrawDTO.getToken().isEmpty() || withdrawDTO.getToken() == null){
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }
        int otp = Integer.parseInt(withdrawDTO.getToken());

        if (otp != otpService.getOtp(sender.getPhone())) {
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        } else {
            long amount = withdrawDTO.getMoney();

            if (amount > sender.getAccountBalance()) {
                return new ResponseEntity<>("Số dư không đủ để thực hiện giao dịch!", HttpStatus.BAD_REQUEST);
            }
            long newSenderAccountBalance = sender.getAccountBalance() - amount;
            sender.setAccountBalance(newSenderAccountBalance);
            userRepository.save(sender);

            withdrawDTO.setUser(mapper.map(sender, UserDTO.class));
            WithdrawDTO dto = withdrawService.createWithdraw(withdrawDTO);

            if(dto.getType() == 1){
                return new ResponseEntity<>("Yêu cầu của bạn được chấp thuận. Mời bạn đến trụ sở của ReBroLand tại Số 1, Lê Đại Hành, Hải Phòng để nhận lại tiền!", HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("Yêu cầu của bạn được chấp thuận. Chúng tôi sẽ gửi tiền cho bạn trong vòng 24h.", HttpStatus.CREATED);
            }
        }

    }


    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
    private User getUserFromToken(String token) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        return user;
    }
}
