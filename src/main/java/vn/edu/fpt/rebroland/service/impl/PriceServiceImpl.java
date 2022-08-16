package vn.edu.fpt.rebroland.service.impl;


import vn.edu.fpt.rebroland.entity.Price;
import vn.edu.fpt.rebroland.payload.ListPrice;
import vn.edu.fpt.rebroland.payload.PriceDTO;
import vn.edu.fpt.rebroland.repository.PriceRepository;
import vn.edu.fpt.rebroland.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<String, Object> getListPostPrice() {
        Price price = priceRepository.getCurrentPostPrice();
        List<Integer> listPrice = priceRepository.getListPostPrice();
        Map<String, Object> map = new HashMap<>();
        map.put("currentPrice", price);
        map.put("listPrice", listPrice);
        return map;
    }

    @Override
    public void createPrice(ListPrice list) {
        for (PriceDTO priceDTO: list.getLists()) {
            Price p = priceRepository.getPrice(priceDTO.getTypeId(), priceDTO.getUnitDate());
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            priceDTO.setStartDate(date);

            if(priceDTO.getPrice() == p.getPrice()){
                if(priceDTO.getDiscount() == p.getDiscount()){
                    break;
                }else{
                    priceDTO.setStatus(true);
                    Price price = priceRepository.save(mapToEntity(priceDTO));

                    p.setStatus(false);
                    priceRepository.save(p);
//                    return mapToDTO(price);
                }
            }else{
                priceDTO.setStatus(true);
                Price price = priceRepository.save(mapToEntity(priceDTO));

                p.setStatus(false);
                priceRepository.save(p);
//                return mapToDTO(price);
            }
        }

    }

    @Override
    public PriceDTO createPostPrice(PriceDTO priceDTO) {
        Price p = priceRepository.getPrice(priceDTO.getTypeId(), priceDTO.getUnitDate());
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        priceDTO.setStartDate(date);

        if(priceDTO.getPrice() == p.getPrice()){
            if(priceDTO.getDiscount() == p.getDiscount()){
                return null;
            }else{
                priceDTO.setStatus(true);
                Price price = priceRepository.save(mapToEntity(priceDTO));

                p.setStatus(false);
                priceRepository.save(p);
                return mapToDTO(price);
            }
        }else{
            priceDTO.setStatus(true);
            Price price = priceRepository.save(mapToEntity(priceDTO));

            p.setStatus(false);
            priceRepository.save(p);
            return mapToDTO(price);
        }
    }

    @Override
    public PriceDTO getPriceByTypeIdAndUnitDate(int typeId, int unitDate) {
        Price price = priceRepository.getPriceByTypeIdAndUnitDate(typeId, unitDate);
        return mapToDTO(price);
    }

    private PriceDTO mapToDTO(Price price) {
        return mapper.map(price, PriceDTO.class);
    }

    private Price mapToEntity(PriceDTO priceDTO) {
        return mapper.map(priceDTO, Price.class);
    }
}
