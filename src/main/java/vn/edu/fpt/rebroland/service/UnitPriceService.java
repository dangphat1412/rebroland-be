package vn.edu.fpt.rebroland.service;


import vn.edu.fpt.rebroland.payload.DirectionDTO;
import vn.edu.fpt.rebroland.payload.UnitPriceDTO;

import java.util.List;

public interface UnitPriceService {

    List<UnitPriceDTO> getAllUnitPrices();

    UnitPriceDTO getUnitPriceById(Integer id);
}
