package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.*;

import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.UserService;
import com.pusher.rest.Pusher;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper mapper;

    private AvgRateRepository rateRepository;
    private PostRepository postRepository;
    private NotificationService notificationService;
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, AvgRateRepository rateRepository, PostRepository postRepository,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.rateRepository = rateRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
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
        java.sql.Date sqlDate = new java.sql.Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        java.sql.Date date = new java.sql.Date(c.getTimeInMillis());
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
    public List<UserDTO> getAllBroker(String fullName, String ward, String district, String province, List<String> propertyType, int userId) {
        String check = null;
        List<Integer> listType = new ArrayList<>();
        if(propertyType != null && !propertyType.isEmpty()){
            for (String s : propertyType) {
                listType.add(Integer.parseInt(s));
            }
            check = "";
        }

        List<User> listBroker = userRepository.searchBroker(fullName, ward, district, province, check, listType, userId);
        List<User> list = new ArrayList<>();
        for (User user : listBroker) {
            if (!list.contains(user)) {
                list.add(user);
            }
        }
        List<UserDTO> listDto = list.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

        return listDto;
    }

    @Override
    public List<UserDTO> searchBroker(String fullName, String ward, String district, String province, List<String> propertyType, int pageNo, int pageSize, String option, int userId) {
        int sortOption = Integer.parseInt(option);
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        String check = null;
        List<Integer> listType = new ArrayList<>();
        if(propertyType != null && !propertyType.isEmpty()){
            for (String s : propertyType) {
                listType.add(Integer.parseInt(s));
            }
            check = "";
        }


        Page<User> userPage = null;
        //star rate desc
        if(sortOption == 0){
            userPage = userRepository.searchBrokerByStarRateDesc(fullName, ward, district, province, check, listType, pageable, userId);
        }

        List<User> listUser = new ArrayList<>();
        for (User user : userPage.getContent()) {
            if (!listUser.contains(user)) {
                listUser.add(user);
            }
        }

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

        Collections.sort(list, new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO u1, UserDTO u2) {
                return Float.compare(u2.getAvgRate(), u1.getAvgRate());
            }
        });

        return list;

    }

    @Override
    public int changePassword(User user, ChangePasswordDTO changePasswordDTO) {
        if(!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
            return 2;
        }
        if(changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())){
            return 0;
        }
        if(passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(user);
            return 1;
        }else {
            return 2;
        }

    }

    @Override
    public List<UserDTO> getAllUserForAdminPaging(int userId, int pageNo, int pageSize, String keyword, String sortValue, int roleId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        int sortOption = Integer.parseInt(sortValue);
        Page<User> page = null;
        switch (sortOption){
            case 0:
                page = userRepository.getAllUserForAdminPaging(userId, keyword, roleId, pageable);
                break;
            case 1:
                page = userRepository.getAllActiveUserForAdminPaging(userId, keyword, roleId, pageable);
                break;
            case 2:
                page = userRepository.getAllBlockUserForAdminPaging(userId, keyword, roleId, pageable);
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
    public List<UserDTO> getAllUserForAdmin(int userId, String keyword, String sortValue, int roleId) {
        int sortOption = Integer.parseInt(sortValue);
        List<User> listUser = null;
        switch (sortOption){
            case 0:
                listUser = userRepository.getAllUserForAdmin(userId, keyword, roleId);
                break;
            case 1:
                listUser = userRepository.getAllActiveUserForAdmin(userId, keyword, roleId);
                break;
            case 2:
                listUser = userRepository.getAllBlockUserForAdmin(userId, keyword, roleId);
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
        long millis = System.currentTimeMillis();
        java.sql.Date sqlDate = new java.sql.Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        java.sql.Date date = new java.sql.Date(c.getTimeInMillis());
        if(user != null){
            if(user.isBlock()){
                user.setBlock(false);
                Date blockDate = user.getBlockDate();
                user.setBlockDate(null);
                userRepository.save(user);

                List<Post> listPost = postRepository.getAllPostBlockByUserId(user.getId());
                List<Post> listDerivative = new ArrayList<>();
                for (Post post : listPost) {
                    if (post.isBlock()) {
                        if (post.getBlockDate().compareTo(blockDate) == 0) {
                            post.setBlock(false);
                            post.setBlockDate(null);
                            postRepository.save(post);
//                            TextMessageDTO messageDTO = new TextMessageDTO();
//                            String message = "Chúng tôi đã hiển thị lại bài viết của bạn, mã bài viết: " + post.getPostId();
////                            messageDTO.setMessage(message);
////                            template.convertAndSend("/topic/message/" + post.getUser().getId(), messageDTO);
//                            Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
//                            pusher.setCluster("ap1");
//                            pusher.setEncrypted(true);
//                            pusher.trigger("my-channel-" + post.getUser().getId(), "my-event", Collections.singletonMap("message", message));
//
//                            saveNotificationAndUpdateUser(message, post.getUser().getId(), post.getPostId());

                            listDerivative = postRepository.getPostOfOriginalPost(post.getPostId());
                            for (Post p : listDerivative) {
                                if (p.getBlockDate().compareTo(blockDate) == 0 && p.isBlock()) {
                                    p.setBlock(false);
                                    post.setBlockDate(null);
                                    postRepository.save(p);

                                    String message1 = "Chúng tôi đã hiển thị lại bài viết của bạn, mã bài viết: " + p.getPostId();
                                    Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                                    pusher.setCluster("ap1");
                                    pusher.setEncrypted(true);
                                    pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message1));
                                    saveNotificationAndUpdateUser(message1, p.getUser().getId(), p.getPostId(), "OpenDerivativePostStatus");
                                }
                            }
                        }


                    }

                }
                return true;
            }else{
                user.setBlock(true);
                user.setBlockDate(date);
                userRepository.save(user);

//                String message = "Chúng tôi đã khóa tài khoản của bạn. Nếu có thắc mắc xin liên hệ số 0397975445.";
//                    messageDTO.setMessage(message);
//                    template.convertAndSend("/topic/message/" + post.getUser().getId(), messageDTO);
                Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                pusher.setCluster("ap1");
                pusher.setEncrypted(true);
                pusher.trigger("my-channel-" + user.getId(), "my-event", Collections.singletonMap("block", true));

//                saveNotificationAndUpdateUser(message, user.getId(), post.getPostId());


                List<Post> listPost = postRepository.getAllPostUnBlockByUserId(user.getId());
                List<Post> listDerivative = new ArrayList<>();
                for(Post post: listPost){
                    post.setBlock(true);
                    post.setBlockDate(date);
                    postRepository.save(post);

                    listDerivative = postRepository.getPostOfOriginalPost(post.getPostId());
                    for(Post p: listDerivative){
                        if(!p.isBlock()){
                            p.setBlock(true);
                            p.setBlockDate(date);
                            postRepository.save(p);
                            String message1 = "Chúng tôi đã ẩn bài viết mã số " + p.getPostId() + " của bạn. Nếu có thắc mắc xin liên hệ email rebroland@gmail.com";
//                            messageDTO.setMessage(message1);
//                            template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);
                            pusher.setCluster("ap1");
                            pusher.setEncrypted(true);
                            pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message1));

                            saveNotificationAndUpdateUser(message1, p.getUser().getId(), p.getPostId(), "DropDerivativePostStatus");
                        }
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
    public UserDTO getUserById(int id) {
        User user = userRepository.getUserById(id);
        if(user != null){
            return mapToDTO(user);
        }else {
            return null;
        }
    }

    @Override
    public void updatePassword(User user, String newPassword) {
//        if(passwordEncoder.matches(newPassword, user.getPassword())){
//            return false;
//        }else{
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
//        }
    }

    private void saveNotificationAndUpdateUser(String message, int userId, int postId, String type){
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(userId);
        if(message == null){
            notificationDTO.setContent("");
        }else{
            notificationDTO.setContent(message);
        }
//        notificationDTO.setPhone(userRequest.getPhone());
        notificationDTO.setPostId(postId);
        notificationDTO.setType(type);
        notificationService.createContactNotification(notificationDTO);

        //update unread notification user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        int numberUnread = user.getUnreadNotification();
        numberUnread++;
        user.setUnreadNotification(numberUnread);
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
