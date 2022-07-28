package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.UserRateDTO;

public interface UserRateService {
    UserRateDTO createUserRate(UserRateDTO userRateDTO);

    float getStarRateOfUserRated(int userRatedId, int userRoleRatedId);
}
