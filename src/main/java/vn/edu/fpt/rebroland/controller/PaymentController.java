package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.config.PaymentConfig;
//import vn.edu.fpt.rebroland.entity.PaymentBrokerOption;
import vn.edu.fpt.rebroland.payload.PaymentDTO;
//import vn.edu.fpt.rebroland.service.PaymentBrokerOptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/payment")
public class PaymentController {

//    private PaymentBrokerOptionService optionService;
//
//    public PaymentController(PaymentBrokerOptionService optionService) {
//        this.optionService = optionService;
//    }

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO){
        int amount = paymentDTO.getAmount() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_version", PaymentConfig.VERSIONVNPAY);

        return null;
    }

//    @GetMapping("/payment-broker-option")
//    public ResponseEntity<?> getAllPaymentOption(){
//        List<PaymentBrokerOption> list = optionService.getAllOption();
//        return new ResponseEntity<>(list, HttpStatus.OK);
//    }
}
