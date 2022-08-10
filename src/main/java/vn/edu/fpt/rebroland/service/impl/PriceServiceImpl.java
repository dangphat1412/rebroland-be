package vn.edu.fpt.rebroland.service.impl;


import vn.edu.fpt.rebroland.entity.Price;
import vn.edu.fpt.rebroland.payload.PriceDTO;
import vn.edu.fpt.rebroland.repository.PriceRepository;
import vn.edu.fpt.rebroland.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceServiceImpl implements PriceService {
    private PriceRepository priceRepository;
    private ModelMapper mapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper mapper) {
        this.priceRepository = priceRepository;
        this.mapper = mapper;
    }

    @Override
    public PriceDTO getPriceByTypeIdAndStatus(int typeId) {
        Price price = priceRepository.getPriceByTypeIdAndStatus(typeId);
        return mapToDTO(price);
    }

    @Override
    public PriceDTO getPriceBroker(int priceId) {
        Price price = priceRepository.getPriceBroker(priceId);
        return mapToDTO(price);
    }

    @Override
    public List<PriceDTO> getListPriceBroker(int typeId) {
        List<Price> listPrice = priceRepository.getListPriceBroker(typeId);
        return listPrice.stream().map(price -> mapToDTO(price)).collect(Collectors.toList());
    }

    private PriceDTO mapToDTO(Price price) {
        return mapper.map(price, PriceDTO.class);
    }

    private Price mapToEntity(PriceDTO priceDTO) {
        return mapper.map(priceDTO, Price.class);
    }
}
