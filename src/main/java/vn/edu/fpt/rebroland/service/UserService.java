package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ChangePasswordDTO;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(RegisterDTO registerDTO);
    void updatePassword(User user, String newPassword);
    UserDTO createBroker(User user, Role role);
    String updateUser(String phone, UserDTO userDTO);
    List<UserDTO> getAllBrokerPaging(int pageNo, int pageSize);
    List<UserDTO> getAllBroker();
    List<UserDTO> searchBroker(String fullName, String ward, String district, String province,
                               String address, int pageNo, int pageSize, String sortValue);
    boolean changePassword(User user, ChangePasswordDTO changePasswordDTO);
}
