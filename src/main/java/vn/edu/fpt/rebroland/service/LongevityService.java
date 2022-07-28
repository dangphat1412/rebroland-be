package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Longevity;
import vn.edu.fpt.rebroland.payload.DirectionDTO;
import vn.edu.fpt.rebroland.payload.LongevityDTO;

import java.util.List;

public interface LongevityService {
    List<LongevityDTO> getAllLongevity();

    LongevityDTO getLongevityById(Integer id);
}
