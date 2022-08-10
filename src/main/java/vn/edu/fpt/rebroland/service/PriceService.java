package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.PriceDTO;

import java.util.List;

public interface PriceService {
    PriceDTO getPriceByTypeIdAndStatus(int typeId);
//    PriceDTO getPriceBroker(int typeId, int unitDate);

    PriceDTO getPriceBroker(int priceId);
    List<PriceDTO> getListPriceBroker(int typeId);
}
