package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.CareDTO;
import vn.edu.fpt.rebroland.payload.CareResponse;
import vn.edu.fpt.rebroland.payload.ShortPostDTO;
import vn.edu.fpt.rebroland.payload.UserCareDTO;

import java.util.List;

public interface UserCareService {
    UserCareDTO createUserCare(UserCareDTO userCareDTO, User user);

    UserCareDTO updateUserCare(UserCareDTO userCareDTO, int careId);

    void deleteRequiredWithUserCare(int careId);

    UserCareDTO finishTransactionUserCare(int careId);


    CareResponse getUserCareByUserId(int userId, int pageNo, int pageSize);

    List<CareDTO> getByUserId(int userId);

    UserCareDTO getUserCareByCareId(int careId);

    List<ShortPostDTO> getPostCareByCareId(int careId);

    void deletePostCareByPostId(int postId);
}
