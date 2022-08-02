package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.CareDetailResponse;
import vn.edu.fpt.rebroland.payload.UserCareDetailDTO;

import java.util.Date;
import java.util.List;

public interface UserCareDetailService {

    UserCareDetailDTO createUserCareDetail(int careId, UserCareDetailDTO userCareDetailDTO, Date date);

    List<CareDetailResponse> getListUserCareByCareId(int careId);
    UserCareDetailDTO updateUserCareDetail(int detailId);
}
