package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Transactions;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.PaymentRepository;
import vn.edu.fpt.rebroland.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        transactionDTO.setStartDate(date);

        Transactions transactions = mapToEntity(transactionDTO);
        Transactions newTransactions = paymentRepository.save(transactions);
        return mapToDTO(newTransactions);
    }

    @Override
    public PaymentResponse getAllPayments(int pageNumber, int pageSize, String keyword, String sortValue) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int sortOption = Integer.parseInt(sortValue);
        Page<Transactions> pagePayment = null;
        switch (sortOption){
            case 0:
                pagePayment = paymentRepository.findAll(pageable, keyword);
                break;
            case 1:
                pagePayment = paymentRepository.findAllPostPayment(pageable, keyword);
                break;
            case 2:
                pagePayment = paymentRepository.findAllBrokerPayment(pageable, keyword);
                break;
        }
        List<Transactions> listTransactions = pagePayment.getContent();
        List<TransactionDTO> listDto = listTransactions.stream().map(transactions -> mapToDTO(transactions)).collect(Collectors.toList());
        PaymentResponse paymentResponse = new PaymentResponse();

        paymentResponse.setPayments(listDto);
        paymentResponse.setPageNo(pageNumber + 1);
        paymentResponse.setTotalPages(pagePayment.getTotalPages());
        paymentResponse.setTotalResult(pagePayment.getTotalElements());
        return paymentResponse;
    }

    @Override
    public Map<String, Long> getTotalMoney() {
        Long totalPostAmount = paymentRepository.getTotalMoneyFromPost();
        Long totalBrokerAmount = paymentRepository.getTotalMoneyFromBroker();
        Long totalAmount = paymentRepository.getTotalRevenue();
        Map<String, Long> map = new HashMap<>();
        map.put("totalPostAmount", totalPostAmount);
        map.put("totalBrokerAmount", totalBrokerAmount);
        map.put("totalAmount", totalAmount);
        return map;
    }

    private TransactionDTO mapToDTO(Transactions transactions) {
        return mapper.map(transactions, TransactionDTO.class);
    }

    private Transactions mapToEntity(TransactionDTO transactionDTO) {
        return mapper.map(transactionDTO, Transactions.class);
    }
}
