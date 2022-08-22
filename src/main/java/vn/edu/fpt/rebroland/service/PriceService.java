package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.BrokerPriceDTO;
import vn.edu.fpt.rebroland.payload.ListPrice;
import vn.edu.fpt.rebroland.payload.PriceDTO;

import java.util.List;
import java.util.Map;

public interface PriceService {
    PriceDTO getPriceByTypeIdAndStatus(int typeId);
//    PriceDTO getPriceBroker(int typeId, int unitDate);

    PriceDTO getPriceBroker(int priceId);
    List<PriceDTO> getListPriceBroker(int typeId);
    Map<String, Object> getListPostPrice();
    Map<String, Object> createBrokerPrice(BrokerPriceDTO brokerPriceDTO);
    List<Integer> getListBrokerPrice();
    void createPrice(ListPrice priceDTO);
    PriceDTO createPostPrice(PriceDTO priceDTO);
    PriceDTO getPriceByTypeIdAndUnitDate(int typeId, int unitDate);
}
