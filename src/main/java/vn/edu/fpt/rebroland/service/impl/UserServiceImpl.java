package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.payload.ChangePasswordDTO;
import vn.edu.fpt.rebroland.payload.RegisterDTO;
import vn.edu.fpt.rebroland.payload.UserDTO;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper mapper;

    private AvgRateRepository rateRepository;
    private PostRepository postRepository;
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, AvgRateRepository rateRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.rateRepository = rateRepository;
        this.postRepository = postRepository;
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

        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        user.setStartDate(date);

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
    public List<UserDTO> getAllUserForAdminPaging(int userId, int pageNo, int pageSize, String keyword, String sortValue) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        int sortOption = Integer.parseInt(sortValue);
        Page<User> page = null;
        switch (sortOption){
            case 0:
                page = userRepository.getAllUserForAdminPaging(userId, keyword, pageable);
                break;
            case 1:
                page = userRepository.getAllActiveUserForAdminPaging(userId, keyword, pageable);
                break;
            case 2:
                page = userRepository.getAllBlockUserForAdminPaging(userId, keyword, pageable);
                break;
        }

        List<User> listUser = page.getContent();
        List<UserDTO> list = new ArrayList<>();
        UserDTO userDTO = null;

        Role role = roleRepository.findByName("BROKER").get();
        for (User user : listUser) {
            userDTO = mapToDTO(user);
            Set<Role> setRole = user.getRoles();
            if(setRole.contains(role)){
                userDTO.setBroker(true);
            }


            list.add(userDTO);
        }
        return list;
    }

    @Override
    public List<UserDTO> getAllUserForAdmin(int userId, String keyword, String sortValue) {
        int sortOption = Integer.parseInt(sortValue);
        List<User> listUser = null;
        switch (sortOption){
            case 0:
                listUser = userRepository.getAllUserForAdmin(userId, keyword);
                break;
            case 1:
                listUser = userRepository.getAllActiveUserForAdmin(userId, keyword);
                break;
            case 2:
                listUser = userRepository.getAllBlockUserForAdmin(userId, keyword);
                break;
        }

        return listUser.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());
    }

    @Override
    public boolean changeStatusOfUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id: " + userId));
        Role role = roleRepository.findByName("ADMIN").get();
        if(user.getRoles().contains(role)){
            return false;
        }
        if(user != null){
            if(user.isBlock()){
                user.setBlock(false);
                userRepository.save(user);
                return true;
            }else{
                user.setBlock(true);
                userRepository.save(user);
                List<Post> listPost = postRepository.getAllPostActiveByUserId(user.getId());
                List<Post> listDerivative = new ArrayList<>();
                for(Post post: listPost){
                    post.setStatus(new Status(4));
                    postRepository.save(post);
                    listDerivative = postRepository.getDerivativePostOfOriginalPost(post.getPostId());
                    for(Post p: listDerivative){
//                        p.setStatus();
                    }

                }

                return true;
            }
        }else {
            return false;
        }

    }

    @Override
    public UserDTO getUserByPhone(String phone) {
        User user = userRepository.getUserByPhone(phone);
        if(user != null){
            return mapToDTO(user);
        }else {
            return null;
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
