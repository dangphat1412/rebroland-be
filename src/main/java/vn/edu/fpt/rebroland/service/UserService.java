package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ChangePasswordDTO;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(RegisterDTO registerDTO);
    void updatePassword(User user, String newPassword);
    UserDTO createBroker(User user, Role role);
    int updateUser(String phone, UserDTO userDTO);
    List<UserDTO> getAllBrokerPaging(int pageNo, int pageSize, int userId);
    List<UserDTO> getAllBroker(String fullName, String ward, String district, String province, List<String> listPropertyType, int userId);
    List<UserDTO> searchBroker(String fullName, String ward, String district, String province,
                               List<String> listPropertyType, int pageNo, int pageSize, String sortValue, int userId);
    int changePassword(User user, ChangePasswordDTO changePasswordDTO);
    List<UserDTO> getAllUserForAdminPaging(int userId, int pageNo, int pageSize, String keyword, String sortValue);

    List<UserDTO> getAllUserForAdmin(int userId, String keyword, String sortValue);

    boolean changeStatusOfUser(int userId);

    UserDTO getUserByPhone(String phone);
}
