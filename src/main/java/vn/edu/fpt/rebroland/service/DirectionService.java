package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.DirectionDTO;

import java.util.List;

public interface DirectionService {
    List<DirectionDTO> getAllDirections();

    DirectionDTO getDirectionById(Integer id);
}
