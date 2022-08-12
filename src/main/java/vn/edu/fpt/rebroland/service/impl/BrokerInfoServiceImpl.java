package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.BrokerInfo;
import vn.edu.fpt.rebroland.payload.BrokerInfoDTO;
import vn.edu.fpt.rebroland.repository.BrokerInfoRepository;
import vn.edu.fpt.rebroland.service.BrokerInfoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class BrokerInfoServiceImpl implements BrokerInfoService {
    private ModelMapper mapper;
    private BrokerInfoRepository brokerInfoRepository;

    public BrokerInfoServiceImpl(ModelMapper mapper, BrokerInfoRepository brokerInfoRepository) {
        this.mapper = mapper;
        this.brokerInfoRepository = brokerInfoRepository;
    }


    @Override
    public BrokerInfoDTO createBrokerInfo(BrokerInfoDTO brokerInfoDTO) {
        BrokerInfo brokerInfo = mapToEntity(brokerInfoDTO);
        BrokerInfo newBrokerInfo = brokerInfoRepository.save(brokerInfo);
        return mapToDTO(newBrokerInfo);
    }

    private BrokerInfoDTO mapToDTO(BrokerInfo apartment) {
        return mapper.map(apartment, BrokerInfoDTO.class);
    }

    private BrokerInfo mapToEntity(BrokerInfoDTO apartmentDTO) {
        return mapper.map(apartmentDTO, BrokerInfo.class);
    }

}
