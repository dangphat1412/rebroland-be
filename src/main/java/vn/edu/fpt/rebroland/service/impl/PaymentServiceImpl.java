package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Payment;
import vn.edu.fpt.rebroland.payload.PaymentDTO;
import vn.edu.fpt.rebroland.repository.PaymentRepository;
import vn.edu.fpt.rebroland.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;

    private ModelMapper mapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ModelMapper mapper) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        paymentDTO.setDate(date);

        Payment payment = mapToEntity(paymentDTO);
        Payment newPayment = paymentRepository.save(payment);
        return mapToDTO(newPayment);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        List<Payment> listPayment = paymentRepository.findAll();
        return listPayment.stream().map(payment -> mapToDTO(payment)).collect(Collectors.toList());
    }

    private PaymentDTO mapToDTO(Payment payment) {
        return mapper.map(payment, PaymentDTO.class);
    }

    private Payment mapToEntity(PaymentDTO paymentDTO) {
        return mapper.map(paymentDTO, Payment.class);
    }
}
