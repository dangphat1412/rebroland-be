package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(RegisterDTO registerDTO);
    void updatePassword(User user, String newPassword);
    UserDTO createBroker(User user, Role role);
    String updateUser(String phone, UserDTO userDTO);
    List<UserDTO> getAllBroker(int pageNo, int pageSize);
}
