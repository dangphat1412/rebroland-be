package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Direction;
import vn.edu.fpt.rebroland.entity.PropertyType;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.PropertyTypeDTO;
import vn.edu.fpt.rebroland.repository.PropertyTypeRepository;
import vn.edu.fpt.rebroland.service.PropertyTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyTypeServiceImpl implements PropertyTypeService {
    private PropertyTypeRepository propertyTypeRepository;

    private ModelMapper modelMapper;

    public PropertyTypeServiceImpl(PropertyTypeRepository propertyTypeRepository, ModelMapper modelMapper) {
        this.propertyTypeRepository = propertyTypeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PropertyTypeDTO> getAllPropertyType() {
        List<PropertyType> propertyTypeList = propertyTypeRepository.findAll();
        return propertyTypeList.stream().map(propertyType -> mapToDTO(propertyType)).collect(Collectors.toList());
    }

    @Override
    public PropertyTypeDTO getPropertyTypeById(Integer id) {
        PropertyType propertyType = propertyTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PropertyType", "id", id));
        return mapToDTO(propertyType);
    }

    private PropertyTypeDTO mapToDTO(PropertyType propertyType) {
        return modelMapper.map(propertyType, PropertyTypeDTO.class);
    }

    private PropertyType mapToEnity(PropertyTypeDTO propertyTypeDTO) {
        return modelMapper.map(propertyTypeDTO, PropertyType.class);
    }
}
