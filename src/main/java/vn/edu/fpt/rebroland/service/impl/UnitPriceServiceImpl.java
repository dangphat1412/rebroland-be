package vn.edu.fpt.rebroland.service.impl;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.UnitPrice;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.UnitPriceDTO;
import vn.edu.fpt.rebroland.repository.UnitPriceRepository;
import vn.edu.fpt.rebroland.service.UnitPriceService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitPriceServiceImpl implements UnitPriceService {

    private UnitPriceRepository unitPriceRepository;

    private ModelMapper mapper;

    public UnitPriceServiceImpl(UnitPriceRepository unitPriceRepository, ModelMapper mapper) {
        this.unitPriceRepository = unitPriceRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UnitPriceDTO> getAllUnitPrices() {
        List<UnitPrice> unitPriceList = unitPriceRepository.findAll();
        return unitPriceList.stream().map(unitPrice -> mapToDTO(unitPrice)).collect(Collectors.toList());
    }

    @Override
    public UnitPriceDTO getUnitPriceById(Integer id) {
        UnitPrice unitPrice = unitPriceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("UnitPrice", "id", id));
        return mapToDTO(unitPrice);
    }

    private UnitPriceDTO mapToDTO(UnitPrice unitPrice) {
        return mapper.map(unitPrice, UnitPriceDTO.class);
    }

    private UnitPrice mapToEntity(UnitPriceDTO unitPriceDTO) {
        return mapper.map(unitPriceDTO, UnitPrice.class);
    }
}
