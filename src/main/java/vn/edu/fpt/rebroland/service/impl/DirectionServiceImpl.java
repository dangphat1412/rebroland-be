package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Direction;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.DirectionDTO;
import vn.edu.fpt.rebroland.repository.DirectionRepository;
import vn.edu.fpt.rebroland.service.DirectionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectionServiceImpl implements DirectionService {


    private DirectionRepository directionRepository;

    private ModelMapper modelMapper;

    public DirectionServiceImpl(DirectionRepository directionRepository, ModelMapper modelMapper) {
        this.directionRepository = directionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<DirectionDTO> getAllDirections() {
        List<Direction> directionList = directionRepository.findAll();
        return directionList.stream().map(direction -> mapToDTO(direction)).collect(Collectors.toList());
    }

    @Override
    public DirectionDTO getDirectionById(Integer id) {
        Direction direction = directionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Direction", "id", id));
        return mapToDTO(direction);
    }

    private DirectionDTO mapToDTO(Direction direction) {
        return modelMapper.map(direction, DirectionDTO.class);
    }

    private Direction mapToEntity(DirectionDTO directionDTO) {
        return modelMapper.map(directionDTO, Direction.class);
    }

}
