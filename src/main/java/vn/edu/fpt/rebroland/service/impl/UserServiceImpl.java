package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;

import vn.edu.fpt.rebroland.payload.ChangePasswordDTO;
import vn.edu.fpt.rebroland.payload.PostDTO;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    public List<UserDTO> getAllBrokerPaging(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> listBroker = userRepository.getAllBrokerPaging(pageable);
        List<UserDTO> list = listBroker.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<UserDTO> getAllBroker() {
        List<User> listBroker = userRepository.getAllBroker();
        List<UserDTO> list = listBroker.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<UserDTO> searchBroker(String fullName, String ward, String district, String province, String address, int pageNo, int pageSize, String option) {
        int sortOption = Integer.parseInt(option);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = null;
        //star rate desc
        if(sortOption == 0){
            userPage = userRepository.searchBrokerByStarRateDesc(fullName, ward, district, province, address, pageable);
        }
        //star rate asc
        if(sortOption == 1){
            userPage = userRepository.searchBrokerByStarRateAsc(fullName, ward, district, province, address, pageable);
        }
        //name A-Z
        if(sortOption == 2){
            userPage = userRepository.searchBrokerByNameAsc(fullName, ward, district, province, address, pageable);
        }
        //name Z-A
        if(sortOption == 3){
            userPage = userRepository.searchBrokerByNameDesc(fullName, ward, district, province, address, pageable);
        }
        List<User> listUser = userPage.getContent();
        List<UserDTO> listUserDto = listUser.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());
        return listUserDto;

    }

    @Override
    public boolean changePassword(User user, ChangePasswordDTO changePasswordDTO) {
        if(passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(user);
            return true;
        }else {
            return false;
        }

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
