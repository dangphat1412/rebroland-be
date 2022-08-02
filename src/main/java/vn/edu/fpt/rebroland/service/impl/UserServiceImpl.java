package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.AvgRate;
import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ChangePasswordDTO;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper mapper;

    private AvgRateRepository rateRepository;
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, AvgRateRepository rateRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.rateRepository = rateRepository;
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
    public int updateUser(String phone, UserDTO userDTO) {
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

            Date date = userDTO.getDob();
//            if(date != null){
//            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = formater.parse(dob);
//            final Date date = new Date();
//                final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
//                final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
//                final TimeZone utc = TimeZone.getTimeZone("UTC");
//                sdf.setTimeZone(utc);
//                date = sdf.parse(dob);
//            System.out.println(sdf.format(date));
//            }


            user.setDob(date);
            user.setDescription(userDTO.getDescription());
            user.setEmail(userDTO.getEmail());
            user.setFacebookLink(userDTO.getFacebookLink());
            user.setZaloLink(userDTO.getZaloLink());

            userRepository.save(user);
            return 1;
        }catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<UserDTO> getAllBrokerPaging(int pageNo, int pageSize, int userId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> listBroker = userRepository.getAllBrokerPaging(userId, pageable);
        List<UserDTO> list = listBroker.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<UserDTO> getAllBroker(String fullName, String ward, String district, String province, List<String> propertyType) {
        String check = null;
        List<Integer> listType = new ArrayList<>();
        if(propertyType != null){
            for (String s : propertyType) {
                listType.add(Integer.parseInt(s));
            }
            check = "";
        }

        List<User> listBroker = userRepository.searchBroker(fullName, ward, district, province, check, listType);
        List<UserDTO> list = listBroker.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<UserDTO> searchBroker(String fullName, String ward, String district, String province, List<String> propertyType, int pageNo, int pageSize, String option) {
        int sortOption = Integer.parseInt(option);
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        String check = null;
        List<Integer> listType = new ArrayList<>();
        if(propertyType != null){
            for (String s : propertyType) {
                listType.add(Integer.parseInt(s));
            }
            check = "";
        }


        Page<User> userPage = null;
        //star rate desc
        if(sortOption == 0){
            userPage = userRepository.searchBrokerByStarRateDesc(fullName, ward, district, province, check, listType, pageable);
        }
        //giao dich thanh cong desc
//        if(sortOption == 1){
//            userPage = userRepository.searchBrokerByStarRateAsc(fullName, ward, district, province, address, pageable);
//        }

        List<User> listUser = userPage.getContent();

        List<UserDTO> list = new ArrayList<>();
        UserDTO userDTO = null;
        AvgRate avgRate = null;
        for (User user : listUser) {
            userDTO = mapToDTO(user);
            avgRate = rateRepository.getAvgRateByUserIdAndRoleId(user.getId(), 3);
            if(avgRate != null){
                userDTO.setAvgRate(avgRate.getAvgRate());
            }else{
                userDTO.setAvgRate(0);
            }

            list.add(userDTO);
        }
        return list;

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
