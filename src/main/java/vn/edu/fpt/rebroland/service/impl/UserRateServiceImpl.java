package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.UserRate;
import vn.edu.fpt.rebroland.payload.UserRateDTO;
import vn.edu.fpt.rebroland.repository.UserRateRepository;
import vn.edu.fpt.rebroland.service.UserRateService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

@Service
public class UserRateServiceImpl implements UserRateService {
    private UserRateRepository userRateRepository;
    private ModelMapper mapper;

    public UserRateServiceImpl(UserRateRepository userRateRepository, ModelMapper mapper) {
        this.userRateRepository = userRateRepository;
        this.mapper = mapper;
    }


    @Override
    public UserRateDTO createUserRate(UserRateDTO userRateDTO) {
        if(userRateDTO.getUserId() == userRateDTO.getUserRated()){
            return null;
        }

        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        userRateDTO.setStartDate(date);

        UserRate userRate = mapToEntity(userRateDTO);
        UserRate newUserRate = userRateRepository.save(userRate);
        return mapToDTO(newUserRate);
    }

    @Override
    public float getStarRateOfUserRated(int userRatedId, int userRoleRatedId) {
        List<UserRate> starRate = userRateRepository.getStarRateOfUserRated(userRatedId, userRoleRatedId);
        if(starRate.size() == 0){
            return 0;
        }
        float average = 0;
        for (UserRate i : starRate) {
            average += i.getStarRate();
        }
        DecimalFormat df = new DecimalFormat("#.0");

        return Float.parseFloat(df.format(average / starRate.size()));
    }

    private UserRateDTO mapToDTO(UserRate userRate){
        UserRateDTO userRateDTO = mapper.map(userRate, UserRateDTO.class);
        return userRateDTO;
    }

    private UserRate mapToEntity(UserRateDTO userRateDTO) {
        UserRate userRate = mapper.map(userRateDTO, UserRate.class);
        return userRate;
    }
}
