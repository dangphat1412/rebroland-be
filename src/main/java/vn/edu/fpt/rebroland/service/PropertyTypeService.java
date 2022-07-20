package vn.edu.fpt.rebroland.service;


import vn.edu.fpt.rebroland.payload.PropertyTypeDTO;

import java.util.List;

public interface PropertyTypeService {
    List<PropertyTypeDTO> getAllPropertyType();
    PropertyTypeDTO getPropertyTypeById(Integer id);


}
