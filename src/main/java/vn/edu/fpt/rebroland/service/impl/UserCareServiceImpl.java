package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.UserCare;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserCareRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.UserCareService;
import com.pusher.rest.Pusher;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCareServiceImpl implements UserCareService {
    private UserCareRepository userCareRepository;

    private ModelMapper modelMapper;
    private UserRepository userRepository;

    private PostRepository postRepository;
    private NotificationService notificationService;

    public UserCareServiceImpl(UserCareRepository userCareRepository, ModelMapper modelMapper,
                               UserRepository userRepository, PostRepository postRepository,
                               NotificationService notificationService) {
        this.userCareRepository = userCareRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    private UserCareDTO mapToDTO(UserCare userCare) {
        return modelMapper.map(userCare, UserCareDTO.class);
    }

    private CareDTO mapToDTO1(UserCare userCare) {
        return modelMapper.map(userCare, CareDTO.class);
    }

    private UserCare mapToEntity(UserCareDTO userCareDTO) {
        return modelMapper.map(userCareDTO, UserCare.class);
    }

    @Override
    public UserCareDTO createUserCare(UserCareDTO userCareDTO, User user, UserCare userCareWithOnlyUserCaredId, int check) {
        UserCare userCare = mapToEntity(userCareDTO);
        userCare.setUser(user);
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        Integer postId = userCareDTO.getPostId();
        if (check == 1) { // insert duplicate user-care and post not duplicate
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
            userCareWithOnlyUserCaredId.getPosts().add(post);
            userCare = userCareWithOnlyUserCaredId;
        }
        if (check == 2) { // insert new user-care not duplicate
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
            userCare.setStartDate(date);
            userCare.setPosts(Collections.singleton(post));
        }
        if (check == 3) {
            userCare.setStartDate(date);
        }
        userCare.setStatus(false);
        UserCare newUserCare = userCareRepository.save(userCare);
        return mapToDTO(newUserCare);
    }

    @Override
    public UserCareDTO createNewUserCare(UserCareDTO userCareDTO, UserDTO dto) {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        userCareDTO.setStartDate(date);
        userCareDTO.setStatus(false);
        UserCare userCare = mapToEntity(userCareDTO);
        UserCare newUserCare = userCareRepository.save(userCare);
        UserCareDTO careDTO = mapToDTO(newUserCare);
        careDTO.setUser(dto);
        return careDTO;
    }

    @Override
    public UserCareDTO updateUserCare(UserCareDTO userCareDTO, int careId) {
        UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
        userCare.setSummarize(userCareDTO.getSummarize());
        UserCare newUserCare = userCareRepository.save(userCare);
        return mapToDTO(newUserCare);
    }

    @Override
    public void deleteRequiredWithUserCare(int careId) {
        try {
            userCareRepository.deletePostCareById(careId);
            userCareRepository.deleteUserCareDetailByCareId(careId);
            userCareRepository.deleteUserCareById(careId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserCareDetailById(int detailId) {
        try {
            userCareRepository.deleteUserCareDetailById(detailId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserCareDTO finishTransactionUserCare(int careId) {
        UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
        userCare.setStatus(true);
        UserCareDTO dto = mapToDTO(userCareRepository.save(userCare));

        int userId = userCare.getUser().getId();
        int userCaredId = userCare.getUserCaredId();
        User userCared = userRepository.getUserById(userCaredId);

        //send notification to user
//        TextMessageDTO messageDTO = new TextMessageDTO();
        String message = "Việc chăm sóc khách hàng đã kết thúc. Vui lòng đánh giá broker!";
//        messageDTO.setMessage(message);
//        template.convertAndSend("/topic/message/" + userCaredId, messageDTO);
        Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
        pusher.setCluster("ap1");
        pusher.setEncrypted(true);
        pusher.trigger("my-channel-" + userCaredId, "my-event", Collections.singletonMap("message", message));


        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(userCaredId);
        notificationDTO.setContent(message);
//        notificationDTO.setPhone(user.getPhone());
        notificationDTO.setSender(userId);
        notificationDTO.setType("FinishTakeCare");
        notificationService.createContactNotification(notificationDTO);

        int numberUnread = userCared.getUnreadNotification();
        numberUnread++;
        userCared.setUnreadNotification(numberUnread);
        userRepository.save(userCared);

        return dto;
    }

    @Override
    public CareResponse getUserCareByUserId(int userId, String keyword, String status, int pageNo, int pageSize) {
        String sortByStartDate = "start_date";
        String sortDir = "desc";
        Sort sortStartDate = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortByStartDate).ascending() : Sort.by(sortByStartDate).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortStartDate);
        int statusId = Integer.parseInt(status);
        Page<UserCare> userCares = null;
        switch (statusId){
            case 0:
                userCares = userCareRepository.getUserCareByUserIdAndStatusFalse(pageable, userId, keyword);
                break;
            case 1:
                userCares = userCareRepository.getUserCareByUserIdAndStatusTrue(pageable, userId, keyword);
                break;
            case 2:
                userCares = userCareRepository.getUserCareByUserId(pageable, userId, keyword);
                break;
        }
//        Page<UserCare> userCares = userCareRepository.getUserCareByUserId(pageable, userId, keyword);
//        List<UserCare> userCareList = userCareRepository.getListUserCareByUserId(userId);
//        List<UserCareDTO> userCareDTOList = userCares.getContent().stream().
//                map(contact -> mapToDTO(contact)).collect(Collectors.toList());


        List<UserCareDTO> userCareDTOList = new ArrayList<>();
        for(UserCare userCare: userCares){
            UserCareDTO userCareDTO = mapToDTO(userCare);
            int userCaredId = userCareDTO.getUserCaredId();
            User userCared =  userRepository.findById(userCaredId).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with id: " + userCaredId));
            userCareDTO.setUser(modelMapper.map(userCared, UserDTO.class));
            userCareDTOList.add(userCareDTO);
        }

        CareResponse careResponse = new CareResponse();
        careResponse.setTotalResult(userCares.getTotalElements());
        careResponse.setCares(userCareDTOList);
        careResponse.setPageNo(pageNo + 1);
        careResponse.setTotalPages(userCares.getTotalPages());
        return careResponse;
    }

    @Override
    public List<CareDTO> getByUserId(int userId) {
        List<UserCare> userCares = userCareRepository.getListUserCareByUserId(userId);
        return userCares.stream().map(userCare -> mapToDTO1(userCare)).collect(Collectors.toList());

    }

    @Override
    public UserCareDTO getUserCareByCareId(int careId) {
        UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
        UserCareDTO userCareDTO = mapToDTO(userCare);

        User userCared = userRepository.findById(userCare.getUserCaredId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userCare.getUserCaredId()));
        userCareDTO.setUser(modelMapper.map(userCared, UserDTO.class));
        return userCareDTO;

    }

    @Override
    public List<ShortPostDTO> getPostCareByCareId(int careId) {
        List<Post> listPosts = postRepository.getListPostCare(careId);

        List<ShortPostDTO> list = listPosts.stream().map(post -> modelMapper.map(post, ShortPostDTO.class)).collect(Collectors.toList());

        return list;
    }

    @Override
    public void deletePostCareByPostId(int postId) {
        try {
            userCareRepository.deletePostCareByPostId(postId);
        } catch (Exception e) {

        }
    }

    public void setDataUserCare(UserCare userCare, UserCare oldUserCare) {
        userCare.setCareId(oldUserCare.getCareId());
        userCare.setUserCaredId(oldUserCare.getUserCaredId());
        userCare.setStartDate(oldUserCare.getStartDate());
        userCare.setSummarize(oldUserCare.getSummarize());
    }
}

//        try {
//            if (postId != null) {
//                UserCare oldUserCare = userCareRepository.findUserCareByUserCaredIdAndPostId(userCareDTO.getUserCaredId(), userCareDTO.getPostId());
//                if (oldUserCare == null) {
//                    UserCare oldUserCared = userCareRepository.findUserCareByUserCaredId(userCareDTO.getUserCaredId());
//                    if (oldUserCared != null) {
//                        //{ add more post in post care
//
//
//
//                    } else { // insert new user-care not duplicate
//                        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
//                        userCare.setStartDate(date);
//                        userCare.setPosts(Collections.singleton(post));
//                    }
//                }
////                else {  // duplicate phone and post id
////                    setDataUserCare(userCare, oldUserCare);
////                    userCare.setPosts(oldUserCare.getPosts());
////                }
//            } else {
//                UserCare oldUserCared = userCareRepository.findUserCareByUserCaredId(userCareDTO.getUserCaredId());
//                if (userCareWithOnlyUserCaredId != null) {
//                    userCare = userCareWithOnlyUserCaredId;
//                } else {
//                    userCare.setStartDate(date);
//                }
//
//            }
//        } catch (Exception e) {
//
//        }
//        userCare.setUserCaredId(userCareDTO.getUserCaredId());