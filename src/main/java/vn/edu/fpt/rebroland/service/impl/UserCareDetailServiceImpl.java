package vn.edu.fpt.rebroland.service.impl;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.UserCareDetail;
import vn.edu.fpt.rebroland.payload.UserCareDetailDTO;
import vn.edu.fpt.rebroland.repository.UserCareDetailRepository;

@Service
public class UserCareDetailServiceImpl {
    private UserCareDetailRepository userCareDetailRepository;

    private ModelMapper modelMapper;

    public UserCareDetailServiceImpl(UserCareDetailRepository userCareDetailRepository, ModelMapper modelMapper) {
        this.userCareDetailRepository = userCareDetailRepository;
        this.modelMapper = modelMapper;
    }

    private UserCareDetailDTO mapToDTO(UserCareDetail userCareDetail) {
        return modelMapper.map(userCareDetail, UserCareDetailDTO.class);
    }

    private UserCareDetail mapToEntity(UserCareDetailDTO userCareDetailDTO) {
        return modelMapper.map(userCareDetailDTO, UserCareDetail.class);
    }
}
