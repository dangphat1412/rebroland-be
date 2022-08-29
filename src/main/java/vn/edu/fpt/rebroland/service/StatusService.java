package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.StatusDTO;


import java.util.List;

public interface StatusService {
    List<StatusDTO> getAllStatus();
    StatusDTO getStatusById(Integer id);
}
