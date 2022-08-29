package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.AvgRate;
import vn.edu.fpt.rebroland.entity.UserRate;
import vn.edu.fpt.rebroland.payload.RateDTO;
import vn.edu.fpt.rebroland.payload.UserRateDTO;
import vn.edu.fpt.rebroland.payload.UserRateResponse;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.repository.UserRateRepository;
import vn.edu.fpt.rebroland.service.UserRateService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRateServiceImpl implements UserRateService {
    private UserRateRepository userRateRepository;
    private ModelMapper mapper;

    private AvgRateRepository rateRepository;

    public UserRateServiceImpl(UserRateRepository userRateRepository, ModelMapper mapper,
                               AvgRateRepository rateRepository) {
        this.userRateRepository = userRateRepository;
        this.mapper = mapper;
        this.rateRepository = rateRepository;
    }


    @Override
    public UserRateDTO createUserRate(UserRateDTO userRateDTO) {
        if(userRateDTO.getUserId() == userRateDTO.getUserRated()){
            return null;
        }

        long millis = System.currentTimeMillis();
        java.sql.Date sqlDate = new java.sql.Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        java.sql.Date date = new java.sql.Date(c.getTimeInMillis());
        userRateDTO.setStartDate(date);

        UserRate userRate = mapToEntity(userRateDTO);
        UserRate newUserRate = userRateRepository.save(userRate);

        //insert to average_rates
        int userRatedId = newUserRate.getUserRated();
        int userRoleRatedId = newUserRate.getUserRoleRated();
        List<UserRate> starRate = userRateRepository.getStarRateOfUserRated(userRatedId, userRoleRatedId);
        float average = 0;
        for (UserRate i : starRate) {
            average += i.getStarRate();
        }
//        AvgRate avgRate = new AvgRate(userRatedId, userRoleRatedId, average / starRate.size());
        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userRatedId, userRoleRatedId);

        if(avgRate != null){
            avgRate.setAvgRate((float) Math.round((average / starRate.size()) * 10) / 10);
            rateRepository.save(avgRate);
        }else{
            AvgRate rate = new AvgRate();
            rate.setUserId(userRatedId);
            rate.setRoleId(userRoleRatedId);
            rate.setAvgRate(userRateDTO.getStarRate());
            rateRepository.save(rate);
        }

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
//        AvgRate avgRate = new AvgRate(userRatedId, userRoleRatedId, average / starRate.size());
//        rateRepository.save(avgRate);
        DecimalFormat df = new DecimalFormat("#.0");

        return Float.parseFloat(df.format(average / starRate.size()));
    }

    @Override
    public UserRateResponse getListUserRate(int userRatedId, int userRoleRatedId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserRate> listUserRate = userRateRepository.getListUserRate(userRatedId, userRoleRatedId, pageable);
        List<UserRate> list = listUserRate.getContent();
        List<RateDTO> listDto = list.stream().map(userRate -> mapper.map(userRate, RateDTO.class)).collect(Collectors.toList());
        UserRateResponse response = new UserRateResponse();
        response.setLists(listDto);
        response.setPageNo(pageNumber + 1);
        response.setTotalPages(listUserRate.getTotalPages());
        response.setTotalResult(listUserRate.getTotalElements());
        return response;
    }

    @Override
    public UserRateDTO getUserRateStartDateMax(int userId, int userRatedId, int roleId) {
        UserRate userRate = userRateRepository.getUserRateStartDateMax(userId, userRatedId, roleId);
        if(userRate == null){
            return null;
        }else {
            return mapToDTO(userRate);
        }
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
