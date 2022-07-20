package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Longevity;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.LongevityDTO;
import vn.edu.fpt.rebroland.repository.LongevityRepository;
import vn.edu.fpt.rebroland.service.LongevityService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LongevityServiceImpl implements LongevityService {

    private LongevityRepository longevityRepository;
    private ModelMapper modelMapper;

    public LongevityServiceImpl(LongevityRepository longevityRepository, ModelMapper modelMapper) {
        this.longevityRepository = longevityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LongevityDTO> getAllLongevity() {
        List<Longevity> longevityList = longevityRepository.findAll();
        return longevityList.stream().map(longevity -> mapToDTO(longevity)).collect(Collectors.toList());

    }

    @Override
    public LongevityDTO getLongevityById(Integer id) {
        Longevity longevity = longevityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Longevity", "id", id));
        return mapToDTO(longevity);
    }

    private LongevityDTO mapToDTO(Longevity longevity) {
        return modelMapper.map(longevity, LongevityDTO.class);
    }

    private Longevity mapToEntity(LongevityDTO longevityDTO) {
        return modelMapper.map(longevityDTO, Longevity.class);
    }
}
