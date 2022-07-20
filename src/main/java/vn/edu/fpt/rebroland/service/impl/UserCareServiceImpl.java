package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.UserCare;
import vn.edu.fpt.rebroland.payload.UserCareDTO;
import vn.edu.fpt.rebroland.repository.UserCareRepository;
import vn.edu.fpt.rebroland.service.UserCareService;

@Service
public class UserCareServiceImpl implements UserCareService {
    private UserCareRepository userCareRepository;

    private ModelMapper modelMapper;

    public UserCareServiceImpl(UserCareRepository userCareRepository, ModelMapper modelMapper) {
        this.userCareRepository = userCareRepository;
        this.modelMapper = modelMapper;
    }
    private UserCareDTO mapToDTO(UserCare userCare) {
        return modelMapper.map(userCare, UserCareDTO.class);
    }

    private UserCare mapToEntity(UserCareDTO userCareDTO) {
        return modelMapper.map(userCareDTO, UserCare.class);
    }

}
