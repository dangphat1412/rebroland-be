package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.UserFollowPost;
import vn.edu.fpt.rebroland.payload.DerivativeDTO;
import vn.edu.fpt.rebroland.payload.PostDTO;
import vn.edu.fpt.rebroland.payload.SearchDTO;
import vn.edu.fpt.rebroland.payload.ShortPostDTO;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserFollowPostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.UserFollowPostService;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFollowPostServiceImpl implements UserFollowPostService {

    private UserRepository userRepository;

    private UserFollowPostRepository followPostRepository;

    private PostRepository postRepository;

    private ModelMapper mapper;

    public UserFollowPostServiceImpl(UserRepository userRepository, UserFollowPostRepository followPostRepository, PostRepository postRepository,
                                     ModelMapper mapper) {
        this.userRepository = userRepository;
        this.followPostRepository = followPostRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public HttpStatus createUserFollowPost(String pId, String phone) {
        int postId = 0;
        if(pId != null){
            postId = Integer.parseInt(pId);
        }

        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        Post post = postRepository.findPostByPostId(postId);

        if(post == null){
            return HttpStatus.BAD_REQUEST;
        }

        UserFollowPost followPost = followPostRepository.getUserFollowPost(user.getId(), postId, user.getCurrentRole());
        if(followPost == null){
            if(user.getPosts().contains(post)){
                return HttpStatus.BAD_REQUEST;
            }else{
                int roleId = user.getCurrentRole();
                UserFollowPost follow = new UserFollowPost();
                follow.setUser(user);
                follow.setPost(post);
                follow.setRoleId(roleId);
                followPostRepository.save(follow);
                return HttpStatus.CREATED;
            }
        }else{
            followPostRepository.delete(followPost);
            return HttpStatus.NO_CONTENT;
        }
    }

    @Override
    public List<DerivativeDTO> getFollowPostByUserPaging (String phone, String propertyId, int pageNo, int pageSize) {
        String sortByStartDate = "start_date";
        String sortDir = "desc";
        Sort sortStartDate = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortByStartDate).ascending(): Sort.by(sortByStartDate).descending();

        String check = null;
        int typeId = 0;
        if(propertyId != null){
            typeId = Integer.parseInt(propertyId);
            check = "";
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortStartDate);

        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        Page<Post> listPost = postRepository.getFollowPostIdByUserPaging(user.getId(), user.getCurrentRole(), typeId, check, pageable);

        List<DerivativeDTO> list = listPost.getContent().stream().map(derivativePost -> mapper.map(derivativePost, DerivativeDTO.class)).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<DerivativeDTO> getFollowPostByUser(String phone) {
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        List<Post> listPost = postRepository.getFollowPostIdByUser(user.getId(), user.getCurrentRole());

        List<DerivativeDTO> list = listPost.stream().map(derivativePost -> mapper.map(derivativePost, DerivativeDTO.class)).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<ShortPostDTO> getShortFollowPostByUser(String phone) {
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        List<Post> listPost = postRepository.getFollowPostIdByUser(user.getId(), user.getCurrentRole());

        List<ShortPostDTO> list = listPost.stream().map(derivativePost -> mapper.map(derivativePost, ShortPostDTO.class)).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<SearchDTO> getTop3FollowPost(String phone) {
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        List<Post> listPost = postRepository.getTop3FollowPost(user.getId(), user.getCurrentRole());

        List<PostDTO> list = listPost.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listDto = new ArrayList<>();
        for (PostDTO postDto: list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        return listDto;
    }

    public void setDataToSearchDTO(SearchDTO searchDTO, PostDTO postDTO) {

        searchDTO.setPostId(postDTO.getPostId());
        searchDTO.setArea(postDTO.getArea());
        searchDTO.setTitle(postDTO.getTitle());
        searchDTO.setDescription(postDTO.getDescription());
        searchDTO.setAddress(postDTO.getAddress());

        Date date = postDTO.getStartDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        searchDTO.setStartDate(simpleDateFormat.format(date));

        searchDTO.setPrice(postDTO.getPrice());
        searchDTO.setDistrict(postDTO.getDistrict());
        searchDTO.setWard(postDTO.getWard());
        searchDTO.setProvince(postDTO.getProvince());
        searchDTO.setAddress(postDTO.getAddress());
        searchDTO.setUnitPrice(postDTO.getUnitPrice());
//        searchDTO.setImages(postDTO.getImages());
    }
    private PostDTO mapToDTO(Post post) {
        PostDTO postDTO = mapper.map(post, PostDTO.class);
        return postDTO;
    }
}
