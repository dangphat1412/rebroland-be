package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.UserRateDTO;
import vn.edu.fpt.rebroland.payload.UserRateResponse;

public interface UserRateService {
    UserRateDTO createUserRate(UserRateDTO userRateDTO);
    float getStarRateOfUserRated(int userRatedId, int userRoleRatedId);
    UserRateResponse getListUserRate(int userRatedId, int userRoleRatedId, int pageNumber, int pageSize);
    UserRateDTO getUserRateStartDateMax(int userId, int userRatedId, int roleId);
}
