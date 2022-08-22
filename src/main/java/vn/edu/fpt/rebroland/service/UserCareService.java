package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.UserCare;
import vn.edu.fpt.rebroland.payload.*;

import java.util.List;

public interface UserCareService {
    UserCareDTO createUserCare(UserCareDTO userCareDTO, User user,
                             UserCare userCareWithOnlyUserCaredId, int check);

    UserCareDTO createNewUserCare(UserCareDTO userCareDTO, UserDTO dto);

    UserCareDTO updateUserCare(UserCareDTO userCareDTO, int careId);

    void deleteRequiredWithUserCare(int careId);

    void deleteUserCareDetailById(int detailId);

    UserCareDTO finishTransactionUserCare(int careId);

    CareResponse getUserCareByUserId(int userId, String keyword, String status, int pageNo, int pageSize);

    List<CareDTO> getByUserId(int userId);

    UserCareDTO getUserCareByCareId(int careId);

    List<ShortPostDTO> getPostCareByCareId(int careId);

    void deletePostCareByPostId(int postId);
}
