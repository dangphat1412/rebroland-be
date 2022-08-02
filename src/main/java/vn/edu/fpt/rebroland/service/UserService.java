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
    List<UserDTO> getAllBroker(String fullName, String ward, String district, String province, List<String> listPropertyType);
    List<UserDTO> searchBroker(String fullName, String ward, String district, String province,
                               List<String> listPropertyType, int pageNo, int pageSize, String sortValue);
    boolean changePassword(User user, ChangePasswordDTO changePasswordDTO);
}
