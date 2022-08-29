package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.*;
import vn.edu.fpt.rebroland.service.UserFollowPostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    private ResidentialHouseRepository houseRepository;
    private ApartmentRepository apartmentRepository;

    public UserFollowPostServiceImpl(UserRepository userRepository, UserFollowPostRepository followPostRepository, PostRepository postRepository,
                                     ModelMapper mapper, ResidentialHouseRepository houseRepository, ApartmentRepository apartmentRepository) {
        this.userRepository = userRepository;
        this.followPostRepository = followPostRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.houseRepository = houseRepository;
        this.apartmentRepository = apartmentRepository;
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

        if((post == null) || (post.getStatus().getId() == 2)){
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
    public SearchResponse getFollowPostByUserPaging (String phone, String propertyId, int pageNo, int pageSize, String option) {
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        String check = null;
        int typeId = 0;
        if(propertyId != null){
            typeId = Integer.parseInt(propertyId);
            check = "";
        }

        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
//        List<DerivativeDTO> listDto = null;
        List<PostDTO> listDto = null;
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getFollowPostIdByUserPaging(user.getId(), user.getCurrentRole(), typeId, check, pageable);
//        listDto = listPosts.getContent().stream().map(derivativePost -> mapper.map(derivativePost, DerivativeDTO.class)).collect(Collectors.toList());
                listDto = listPosts.getContent().stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getFollowPostIdByUserPagingOrderByPriceAsc(user.getId(), user.getCurrentRole(), typeId, check, pageable);
                list = listPosts.getContent();
//                listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
//                return listDto;

                listDto = list.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getFollowPostIdByUserPagingOrderByPriceDesc(user.getId(), user.getCurrentRole(), typeId, check, pageable);
                list = listPosts.getContent();
//                listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
//                return listDto;
                listDto = list.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getFollowPostIdByUserPagingOrderByPricePerSquareAsc(user.getId(), user.getCurrentRole(), typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getFollowPostIdByUserPagingOrderByPricePerSquareDesc(user.getId(), user.getCurrentRole(), typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getFollowPostIdByUserPaging(user.getId(), user.getCurrentRole(), typeId, check, pageable);
                listDto = listPosts.getContent().stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getFollowPostIdByUserPaging(user.getId(), user.getCurrentRole(), typeId, check, pageable);
                listDto = listPosts.getContent().stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
                break;
        }

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto : listDto) {
            SearchDTO dto = new SearchDTO();
            if(postDto.getDirection() != null){
                dto.setDirectionId(postDto.getDirection().getId());
            }else{
                dto.setDirectionId(0);
            }
            setDataToSearchDTO(dto, postDto);
            int postId = postDto.getPostId();
            switch (postDto.getPropertyType().getId()) {
                case 1: // view residential house
                    ResidentialHouse residentialHouse = houseRepository.findByPostId(postId);
                    dto.setNumberOfBedroom(residentialHouse.getNumberOfBedroom());
                    dto.setNumberOfBathroom(residentialHouse.getNumberOfBathroom());
                    break;
                case 2:// view apartment
                    Apartment apartment = apartmentRepository.findByPostId(postId);
                    dto.setNumberOfBedroom(apartment.getNumberOfBedroom());
                    dto.setNumberOfBathroom(apartment.getNumberOfBathroom());
                    break;
                case 3:// view residential land
                    dto.setNumberOfBedroom(0);
                    dto.setNumberOfBathroom(0);
                    break;
            }

            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo + 1);
        searchResponse.setTotalPages(listPosts.getTotalPages());
        searchResponse.setTotalResult(listPosts.getTotalElements());
        return searchResponse;
    }

    @Override
    public List<DerivativeDTO> getFollowPostByUser(String phone, String propertyId) {
        String check = null;
        int typeId = 0;
        if(propertyId != null){
            typeId = Integer.parseInt(propertyId);
            check = "";
        }

        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        List<Post> listPost = postRepository.getAllFollowPostIdByUser(user.getId(), user.getCurrentRole(), typeId, check);

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
    public void deleteFollowByPostId(int postId) {
        try {
            followPostRepository.deleteFollowByPostId(postId);
        }catch (Exception e){
            e.printStackTrace();
        }
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

        if (postDTO.getPrice() != null) {
            searchDTO.setPrice(postDTO.getPrice());
        } else {
            searchDTO.setPrice(0);
        }
        searchDTO.setDistrict(postDTO.getDistrict());
        searchDTO.setWard(postDTO.getWard());
        searchDTO.setProvince(postDTO.getProvince());
        searchDTO.setAddress(postDTO.getAddress());
        searchDTO.setUnitPrice(postDTO.getUnitPrice());
        searchDTO.setUser(postDTO.getUser());
        searchDTO.setThumbnail(postDTO.getThumbnail());
        searchDTO.setOriginalPost(postDTO.getOriginalPost());
//        searchDTO.setImages(postDTO.getImages());
    }
    private PostDTO mapToDTO(Post post) {
        PostDTO postDTO = mapper.map(post, PostDTO.class);
        return postDTO;
    }
}
