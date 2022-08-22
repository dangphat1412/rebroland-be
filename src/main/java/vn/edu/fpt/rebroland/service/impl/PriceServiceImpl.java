package vn.edu.fpt.rebroland.service.impl;


import vn.edu.fpt.rebroland.entity.Price;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.PriceRepository;
import vn.edu.fpt.rebroland.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Map<String, Object> createBrokerPrice(BrokerPriceDTO brokerPriceDTO) {
        Price oneMonthPrice = priceRepository.getPriceByTypeIdAndUnitDate(2, 30);
        Price threeMonthPrice = priceRepository.getPriceByTypeIdAndUnitDate(2, 90);
        Price sixMonthPrice = priceRepository.getPriceByTypeIdAndUnitDate(2, 180);
        Price twelveMonthPrice = priceRepository.getPriceByTypeIdAndUnitDate(2, 360);

        PriceDTO oneMonthDto = createBrokerPriceByMonth(oneMonthPrice, brokerPriceDTO.getBrokerPrice(), brokerPriceDTO.getOneMonthDiscount(), 30);
        PriceDTO threeMonthDto = createBrokerPriceByMonth(threeMonthPrice, brokerPriceDTO.getBrokerPrice() * 3, brokerPriceDTO.getThreeMonthsDiscount(), 90);
        PriceDTO sixMonthDto = createBrokerPriceByMonth(sixMonthPrice, brokerPriceDTO.getBrokerPrice() * 6, brokerPriceDTO.getSixMonthsDiscount(), 180);
        PriceDTO twelveMonthDto = createBrokerPriceByMonth(twelveMonthPrice, brokerPriceDTO.getBrokerPrice() * 12, brokerPriceDTO.getTwelveMonthsDiscount(), 360);

        List<PriceDTO> list = new ArrayList<>();
        list.add(oneMonthDto);
        list.add(threeMonthDto);
        list.add(sixMonthDto);
        list.add(twelveMonthDto);

        Map<String, Object> map = new HashMap<>();
        map.put("currentPrice", list);
        return map;
    }

    private PriceDTO createBrokerPriceByMonth(Price brokerPrice, long price, int discount, int unitDate) {
        try {
            PriceDTO dto = new PriceDTO();
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            dto.setStartDate(date);
            dto.setTypeId(2);
            if (brokerPrice == null) {
                dto.setPrice(price);
                dto.setDiscount(discount);
                dto.setStatus(true);
                dto.setUnitDate(unitDate);
                Price p = mapToEntity(dto);
                Price newPrice = priceRepository.save(p);
                return mapToDTO(newPrice);
            } else {
                if (brokerPrice.getPrice() == price && brokerPrice.getDiscount() == discount) {
                    return mapToDTO(brokerPrice);
                }
                brokerPrice.setStatus(false);
                priceRepository.save(brokerPrice);
                dto.setPrice(price);
                dto.setDiscount(discount);
                dto.setStatus(true);
                dto.setUnitDate(unitDate);
                Price p = mapToEntity(dto);
                Price newPrice = priceRepository.save(p);
                return mapToDTO(newPrice);
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Integer> getListBrokerPrice() {
        List<Integer> listPrice = priceRepository.getListBrokerPrice();
        return listPrice;
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
