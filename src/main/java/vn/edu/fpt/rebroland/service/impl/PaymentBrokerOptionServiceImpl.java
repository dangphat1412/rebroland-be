//package vn.edu.fpt.rebroland.service.impl;
//
//import vn.edu.fpt.rebroland.entity.PaymentBrokerOption;
//import vn.edu.fpt.rebroland.payload.PaymentBrokerOptionDTO;
//import vn.edu.fpt.rebroland.repository.PaymentBrokerOptionRepository;
//import vn.edu.fpt.rebroland.service.PaymentBrokerOptionService;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class PaymentBrokerOptionServiceImpl implements PaymentBrokerOptionService {
//    private PaymentBrokerOptionRepository optionRepository;
//
//    private ModelMapper mapper;
//
//    public PaymentBrokerOptionServiceImpl(PaymentBrokerOptionRepository optionRepository, ModelMapper mapper) {
//        this.optionRepository = optionRepository;
//        this.mapper = mapper;
//    }
//
//    @Override
//    public List<PaymentBrokerOption> getAllOption() {
//        List<PaymentBrokerOption> listOptions = optionRepository.findAll();
//        return null;
//    }
//
//    private PaymentBrokerOptionDTO mapToDTO(PaymentBrokerOption option) {
//        return mapper.map(option, PaymentBrokerOptionDTO.class);
//    }
//    private PaymentBrokerOption mapToEntity(PaymentBrokerOptionDTO optionDTO) {
//        return mapper.map(optionDTO, PaymentBrokerOption.class);
//    }
//}
