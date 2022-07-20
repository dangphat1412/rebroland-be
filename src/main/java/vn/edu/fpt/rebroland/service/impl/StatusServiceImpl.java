package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Status;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.StatusDTO;
import vn.edu.fpt.rebroland.repository.StatusRepository;
import vn.edu.fpt.rebroland.service.StatusService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatusServiceImpl implements StatusService {
    private StatusRepository statusRepository;

    private ModelMapper modelMapper;

    public StatusServiceImpl(StatusRepository statusRepository, ModelMapper modelMapper) {
        this.statusRepository = statusRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<StatusDTO> getAllStatus() {
        List<Status> statusList = statusRepository.findAll();
        return statusList.stream().map(status -> mapToDTO(status)).collect(Collectors.toList());
    }

    @Override
    public StatusDTO getStatusById(Integer id) {
        Status status = statusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Staus", "id", id));
        return mapToDTO(status);
    }

    private StatusDTO mapToDTO(Status Status) {
        return modelMapper.map(Status, StatusDTO.class);
    }

    private Status mapToEntity(StatusDTO statusDTO) {
        return modelMapper.map(statusDTO, Status.class);
    }

}
