package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.UserCare;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.CareDTO;
import vn.edu.fpt.rebroland.payload.CareResponse;
import vn.edu.fpt.rebroland.payload.ShortPostDTO;
import vn.edu.fpt.rebroland.payload.UserCareDTO;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserCareRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.UserCareService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCareServiceImpl implements UserCareService {
    private UserCareRepository userCareRepository;

    private ModelMapper modelMapper;
    private UserRepository userRepository;

    private PostRepository postRepository;

    public UserCareServiceImpl(UserCareRepository userCareRepository, ModelMapper modelMapper,
                               UserRepository userRepository, PostRepository postRepository) {
        this.userCareRepository = userCareRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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
    public UserCareDTO createUserCare(UserCareDTO userCareDTO, User user) {
        UserCare userCare = mapToEntity(userCareDTO);
        userCare.setUser(user);
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        Integer postId = userCareDTO.getPostId();
        try {
            if (postId != null) {
                UserCare oldUserCare = userCareRepository.findUserCareByPhoneAndPostId(userCareDTO.getPhone(), userCareDTO.getPostId());
                UserCare oldPhoneUserCare = userCareRepository.findUserCareByPhone(userCareDTO.getPhone());
                if (oldUserCare == null) {
                    if (oldPhoneUserCare != null) {
                        // add more post in post care
                        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
                        oldPhoneUserCare.getPosts().add(post);
                        userCare = oldPhoneUserCare;

                    } else { // insert new user-care not duplicate
                        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
                        userCare.setStartDate(date);
                        userCare.setPosts(Collections.singleton(post));
                    }
                }
                // duplicate phone and post id
                if (oldUserCare != null && !oldUserCare.isStatus()) {
                    setDataUserCare(userCare, oldUserCare);
                    userCare.setPosts(oldUserCare.getPosts());
                }

            }
        } catch (Exception e) {

        }
        userCare.setStatus(false);
        UserCare newUserCare = userCareRepository.save(userCare);
        return mapToDTO(newUserCare);
    }

    @Override
    public UserCareDTO updateUserCare(UserCareDTO userCareDTO, int careId) {
        UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
        userCare.setFullName(userCareDTO.getFullName());
        userCare.setFullName(userCareDTO.getFullName());
        userCare.setEmail(userCareDTO.getEmail());
        userCare.setSummarize(userCareDTO.getSummarize());
        UserCare newUserCare = userCareRepository.save(userCare);
        return mapToDTO(newUserCare);
    }

    @Override
    public void deleteRequiredWithUserCare(int careId) {
        try {
            userCareRepository.deletePostCareById(careId);
            userCareRepository.deleteUserCareById(careId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserCareDTO finishTransactionUserCare(int careId) {
        UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
        userCare.setStatus(true);
        return mapToDTO(userCareRepository.save(userCare));
    }

    @Override
    public CareResponse getUserCareByUserId(int userId, int pageNo, int pageSize) {
        String sortByStartDate = "start_date";
        String sortDir = "desc";
        Sort sortStartDate = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortByStartDate).ascending() : Sort.by(sortByStartDate).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortStartDate);
        Page<UserCare> userCares = userCareRepository.getUserCareByUserId(pageable, userId);
        List<UserCare> userCareList = userCareRepository.getListUserCareByUserId(userId);
        List<UserCareDTO> userCareDTOList = userCares.getContent().stream().
                map(contact -> mapToDTO(contact)).collect(Collectors.toList());

        CareResponse careResponse = new CareResponse();
        careResponse.setTotalResult(userCareList.size());
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
        return mapToDTO(userCare);
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
        }catch (Exception e){

        }
    }

    public void setDataUserCare(UserCare userCare, UserCare oldUserCare) {
        userCare.setCareId(oldUserCare.getCareId());
        userCare.setPhone(oldUserCare.getPhone());
        userCare.setStartDate(oldUserCare.getStartDate());
        userCare.setFullName(oldUserCare.getFullName());
        userCare.setEmail(oldUserCare.getEmail());
        userCare.setSummarize(oldUserCare.getSummarize());
    }
}