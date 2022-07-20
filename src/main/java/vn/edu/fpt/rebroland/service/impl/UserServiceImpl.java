package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper mapper;
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDTO createUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFullName());
        user.setPhone(registerDTO.getPhone());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singleton(roles));
        user.setCurrentRole(2);
        User newUser = userRepository.save(user);
        return mapToDTO(newUser);
    }

    @Override
    public UserDTO createBroker(User user, Role role){
        user.getRoles().add(role);
        user.setCurrentRole(3);
        User newUser = userRepository.save(user);
        return mapToDTO(newUser);
    }

    @Override
    public String updateUser(String phone, UserDTO userDTO) {
        try{
            User user = userRepository.findByPhone(phone).
                    orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

            user.setFullName(userDTO.getFullName());
            user.setGender(userDTO.getGender());
            user.setAvatar(userDTO.getAvatar());
            user.setAddress(userDTO.getAddress());
            user.setWard(userDTO.getWard());
            user.setDistrict(userDTO.getDistrict());
            user.setProvince(userDTO.getProvince());

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(userDTO.getDob());

            user.setDob(date);
            user.setDescription(userDTO.getDescription());
            user.setEmail(userDTO.getEmail());

            userRepository.save(user);
            return "Chỉnh sửa thông tin cá nhân thành công!";
        }catch (Exception e) {
            return "Chỉnh sửa thông tin cá nhân thất bại!";
        }
    }

    @Override
    public List<UserDTO> getAllBroker(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> listBroker = userRepository.getAllBroker(pageable);
        List<UserDTO> list = listBroker.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

        return list;
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserDTO mapToDTO(User user){
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        return userDTO;
    }

    private User mapToEntity(UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);
        return user;
    }

}
