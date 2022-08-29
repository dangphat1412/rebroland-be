package vn.edu.fpt.rebroland.service.impl;


import vn.edu.fpt.rebroland.entity.Type;
import vn.edu.fpt.rebroland.entity.UserCare;
import vn.edu.fpt.rebroland.entity.UserCareDetail;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.CareDetailResponse;
import vn.edu.fpt.rebroland.payload.UserCareDetailDTO;
import vn.edu.fpt.rebroland.repository.UserCareDetailRepository;
import vn.edu.fpt.rebroland.repository.UserCareRepository;
import vn.edu.fpt.rebroland.service.UserCareDetailService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCareDetailServiceImpl implements UserCareDetailService {
    private UserCareDetailRepository userCareDetailRepository;

    private ModelMapper modelMapper;

    private UserCareRepository userCareRepository;


    public UserCareDetailServiceImpl(UserCareDetailRepository userCareDetailRepository, ModelMapper modelMapper, UserCareRepository userCareRepository) {
        this.userCareDetailRepository = userCareDetailRepository;
        this.modelMapper = modelMapper;
        this.userCareRepository = userCareRepository;
    }

    private UserCareDetailDTO mapToDTO(UserCareDetail userCareDetail) {
        return modelMapper.map(userCareDetail, UserCareDetailDTO.class);
    }

    private UserCareDetail mapToEntity(UserCareDetailDTO userCareDetailDTO) {
        return modelMapper.map(userCareDetailDTO, UserCareDetail.class);
    }

    @Override
    public UserCareDetailDTO createUserCareDetail(int careId, UserCareDetailDTO userCareDetailDTO, java.util.Date date1) {
        UserCareDetail userCareDetail = new UserCareDetail();
        UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("Care", "id", careId));
        long millis = System.currentTimeMillis();
        Date sqlDate = new Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        Date date = new Date(c.getTimeInMillis());
        userCareDetail.setDateCreate(date);
        userCareDetail.setDescription(userCareDetailDTO.getDescription());

        userCareDetail.setUserCare(userCare);
        if (userCareDetailDTO.getType().equalsIgnoreCase("NOTE")) {
            userCareDetail.setType(Type.NOTE);
            userCareDetail.setAppointmentTime(null);
            userCareDetail.setAlertTime(null);

        } else {
            userCareDetail.setAlertTime(userCareDetailDTO.getAlertTime());
            userCareDetail.setType(Type.APPOINTMENT);
            userCareDetail.setAppointmentTime(date1);
        }
        userCareDetail.setStatus(false);


        return mapToDTO(userCareDetailRepository.save(userCareDetail));
    }

    @Override
    public List<CareDetailResponse> getListUserCareByCareId(int careId) {
        List<UserCareDetail> userCareDetails = userCareDetailRepository.getUserCareDetailsByCareId(careId);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return userCareDetails.stream().map(post -> modelMapper.map(post, CareDetailResponse.class)).collect(Collectors.toList());
    }

    @Override
    public UserCareDetailDTO updateUserCareDetail(int detailId) {
        UserCareDetail userCareDetail = userCareDetailRepository.findById(detailId).orElseThrow(() -> new ResourceNotFoundException("Detail", "id", detailId));
        userCareDetail.setStatus(true);
        return mapToDTO(userCareDetail);
    }
}
