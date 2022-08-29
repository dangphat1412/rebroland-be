package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.*;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.PostService;
import com.pusher.rest.Pusher;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private DirectionRepository directionRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private UnitPriceRepository unitPriceRepository;
    private StatusRepository statusRepository;
    private LongevityRepository longevityRepository;
    private ModelMapper mapper;
    private ResidentialHouseRepository houseRepository;
    private ResidentialHouseHistoryRepository houseHistoryRepository;
    private ApartmentRepository apartmentRepository;
    private ApartmentHistoryRepository apartmentHistoryRepository;
    private ResidentialLandRepository landRepository;
    private ResidentialLandHistoryRepository landHistoryRepository;
    private AvgRateRepository rateRepository;
    private RoleRepository roleRepository;
    private NotificationService notificationService;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, DirectionRepository directionRepository,
                           PropertyTypeRepository propertyTypeRepository, UnitPriceRepository unitPriceRepository,
                           StatusRepository statusRepository, LongevityRepository longevityRepository, ModelMapper mapper,
                           ResidentialHouseRepository houseRepository, ResidentialHouseHistoryRepository houseHistoryRepository,
                           ApartmentRepository apartmentRepository, ApartmentHistoryRepository apartmentHistoryRepository,
                           ResidentialLandRepository landRepository, ResidentialLandHistoryRepository landHistoryRepository,
                           AvgRateRepository rateRepository, RoleRepository roleRepository, NotificationService notificationService) {

        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.directionRepository = directionRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        this.unitPriceRepository = unitPriceRepository;
        this.statusRepository = statusRepository;
        this.longevityRepository = longevityRepository;
        this.mapper = mapper;
        this.houseRepository = houseRepository;
        this.houseHistoryRepository = houseHistoryRepository;
        this.apartmentRepository = apartmentRepository;
        this.apartmentHistoryRepository = apartmentHistoryRepository;
        this.landRepository = landRepository;
        this.landHistoryRepository = landHistoryRepository;
        this.rateRepository = rateRepository;
        this.roleRepository = roleRepository;
        this.notificationService = notificationService;
    }

    @Override
    public PostDTO createPost(PostDTO postDTO, int userId, Integer directionId, int propertyTypeId, int unitId, int statusId, Integer longevityId) {
        Post post = mapToEntity(postDTO);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        PropertyType propertyType = propertyTypeRepository.findById(propertyTypeId).orElseThrow(() -> new ResourceNotFoundException("PropertyType", "id", propertyTypeId));
        UnitPrice unitPrice = unitPriceRepository.findById(unitId).orElseThrow(() -> new ResourceNotFoundException("UnitPrice", "id", unitId));
        Status status = statusRepository.findById(statusId).orElseThrow(() -> new ResourceNotFoundException("Status", "id", statusId));
        if (directionId == null) {
            post.setDirection(null);
//            post.getDirection().setId(0);
        } else {
            Direction direction = directionRepository.findById(directionId).orElseThrow(() -> new ResourceNotFoundException("Direction", "id", directionId));
            post.setDirection(direction);
        }
        if (longevityId == null) {
            post.setLongevity(null);
        } else {
            Longevity longevity = longevityRepository.findById(longevityId).orElseThrow(() -> new ResourceNotFoundException("Unit", "id", longevityId));
            post.setLongevity(longevity);
        }

        post.setUser(user);
        post.setPropertyType(propertyType);
        post.setUnitPrice(unitPrice);
        post.setStatus(status);
        post.setBlock(false);
        Post newPost = postRepository.save(post);
        return mapToDTO(newPost);
    }

    @Override
    public SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId, String option, String status) {
        String check = null;
        int typeId = 0;
        if (propertyId != null) {
            check = "";
            typeId = Integer.parseInt(propertyId);
        }


        int sortOption = Integer.parseInt(option);
        int statusId = Integer.parseInt(status);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getPostByUserId(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getPostByUserIdOrderByPriceAsc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getPostByUserIdOrderByPriceDesc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getPostByUserIdOrderByPricePerSquareAsc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getPostByUserIdOrderByPricePerSquareDesc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getPostByUserId(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getPostByUserId(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
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

            if(postDto.getTransactionEndDate() != null){
                dto.setEndDate(postDto.getTransactionEndDate());
            }else{
                dto.setEndDate(null);
            }

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
    public SearchResponse getAllPostByUserId(int pageNo, int pageSize, int userId, String propertyId, String option) {
        String check = null;
        int typeId = 0;
        if (propertyId != null) {
            check = "";
            typeId = Integer.parseInt(propertyId);
        }

        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllPostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllPostByUserIdOrderByPriceAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllPostByUserIdOrderByPriceDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllPostByUserIdOrderByPricePerSquareAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllPostByUserIdOrderByPricePerSquareDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllPostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllPostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
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
    public SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        String check = null;
        int typeId = 0;
        if (propertyId != null) {
            check = "";
            typeId = Integer.parseInt(propertyId);
        }
        Page<Post> posts = postRepository.findByUserIdAndPropertyId(userId, typeId, check, pageable);
        List<Post> listPosts = posts.getContent();
        List<PostDTO> list = listPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listDto = new ArrayList<>();
        for (PostDTO postDto : list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listDto);
        searchResponse.setPageNo(pageNo + 1);
        searchResponse.setTotalPages(posts.getTotalPages());
        searchResponse.setTotalResult(posts.getTotalElements());
        return searchResponse;
    }

    @Override
    public SearchResponse getAllPostForBroker(int pageNo, int pageSize, String option) {
        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAllPostForBroker(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllPostForBrokerOrderByPriceAsc(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllPostForBrokerOrderByPriceDesc(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllPostForBrokerOrderByPricePerSquareAsc(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllPostForBrokerOrderByPricePerSquareDesc(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAllPostForBroker(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAllPostForBroker(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto : listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
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
    public SearchResponse getAllPostByUserId(int pageNo, int pageSize, int userId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> pagePost = postRepository.getAllPostByUserIdPaging(userId, pageable);

        List<PostDTO> listDto = pagePost.getContent().stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto : listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo + 1);
        searchResponse.setTotalPages(pagePost.getTotalPages());
        searchResponse.setTotalResult(pagePost.getTotalElements());
        return searchResponse;
    }

    @Override
    public void blockAllPostByUserId(int userId) {
        List<Post> listPost = postRepository.getAllPostToBlock(userId);
        Status status = new Status(5);
        for (Post post : listPost) {
            post.setStatus(status);
            postRepository.save(post);
        }
    }

    @Override
    public List<BrokerInfoOfPostDTO> getDerivativePostOfOriginalPost(int originalPostId) {
        List<Post> listPost = postRepository.getDerivativePostOfOriginalPost(originalPostId);
        List<BrokerInfoOfPostDTO> listPostDto = new ArrayList<>();
        AvgRate avgRate = null;
        for (Post post : listPost) {
            BrokerInfoOfPostDTO dto = mapper.map(post, BrokerInfoOfPostDTO.class);
            UserDTO userDTO = dto.getUser();
            User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userDTO.getId()));;
            Boolean isBroker = false;
            Set<Role> setRole = user.getRoles();
            Role role = roleRepository.findByName("BROKER").get();
            if(setRole.contains(role)){
                isBroker = true;
            }
            userDTO.setBroker(isBroker);
            avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userDTO.getId(), 3);
            if (avgRate != null) {
                userDTO.setAvgRate(avgRate.getAvgRate());
            } else {
                userDTO.setAvgRate(0);
            }
            dto.setUser(userDTO);
            listPostDto.add(dto);
        }

//        return listPost.stream().map(post -> mapper.map(post, BrokerInfoOfPostDTO.class)).collect(Collectors.toList());
        return listPostDto;
    }


    @Override
    public SearchResponse searchPosts(String ward, String district, String province, String minPrice, String maxPrice,
                                      String minArea, String maxArea, List<String> propertyType, String keyword,
                                      List<String> direction, int bedroom, int pageNo, int pageSize, String option) {
        Long minP = null;
        if (minPrice != null) {
            minP = Long.parseLong(minPrice);
        }

        Long maxP = 0L;
        if (maxPrice == null) {
            maxP = Long.MAX_VALUE;
        } else {
            maxP = Long.parseLong(maxPrice);
        }

        Float maxA = 0f;
        if (maxArea == null) {
            maxA = Float.MAX_VALUE;
        } else {
            maxA = Float.parseFloat(maxArea);
        }

        Float minA = Float.parseFloat(minArea);

        String check = null;
        List<Integer> listId = new ArrayList<>();
        if (direction != null && direction.size() != 0) {
            for (String s : direction) {
                listId.add(Integer.parseInt(s));
            }
            check = "";
        }

        List<Integer> listType = new ArrayList<>();
        for (String s : propertyType) {
            listType.add(Integer.parseInt(s));
        }

        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = new ArrayList<>();
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPriceAsc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPriceDesc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPricePerSquareAsc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPricePerSquareDesc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
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
    public SearchResponse searchOriginalPosts(String ward, String district, String province, String minPrice, String maxPrice,
                                              String minArea, String maxArea, List<String> propertyType, String keyword,
                                              List<String> direction, int bedroom, int pageNo, int pageSize, String option,
                                              int userId) {
        Long minP = null;
        if (minPrice != null) {
            minP = Long.parseLong(minPrice);
        }

        Long maxP = 0L;
        if (maxPrice == null) {
            maxP = Long.MAX_VALUE;
        } else {
            maxP = Long.parseLong(maxPrice);
        }

        Float maxA = 0f;
        if (maxArea == null) {
            maxA = Float.MAX_VALUE;
        } else {
            maxA = Float.parseFloat(maxArea);
        }

        Float minA = Float.parseFloat(minArea);

        String check = null;
        List<Integer> listId = new ArrayList<>();
        if (direction != null) {
            for (String s : direction) {
                listId.add(Integer.parseInt(s));
            }
            check = "";
        }

        List<Integer> listType = new ArrayList<>();
        for (String s : propertyType) {
            listType.add(Integer.parseInt(s));
        }

        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPriceAsc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPriceDesc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPricePerSquareAsc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.searchPostOrderByPricePerSquareDesc(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }
        List<SearchDTO> listSearchDto = new ArrayList<>();


        for (PostDTO postDto : listDto) {
//            Post p = postRepository.getAllDerivativeByUserId(userId, postDto.getPostId());
//            if(p != null){
//                break;
//            }
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
    public List<PostDTO> getPostByPropertyTypeId(int propertyTypeId) {
        return null;
    }

    @Override
    public PostDTO getPostByPostId(int postId) {
        Post post = postRepository.findPostByPostId(postId);
        if(post != null){
            return mapToDTO(post);
        }else{
            return null;
        }

    }

    @Override
    public PostDTO getActiveOrFinishPostById(int postId) {
        Post post = postRepository.getActiveOrFinishPostById(postId);
        if(post != null){
            return mapToDTO(post);
        }else{
            return null;
        }
    }

    @Override
    public PostDTO findPostByPostId(int postId) {
        Post post = postRepository.findPostById(postId);
        if(post != null){
            return mapToDTO(post);
        }else{
            return null;
        }
    }

    @Override
    public PostDTO getAllPostByPostId(int postId) {
        Post post = postRepository.findAllPostByPostId(postId);
        if(post != null){
            return mapToDTO(post);
        }else{
            return null;
        }
    }

    @Override
    public PostDTO getDerivativePostByPostId(int postId) {
        Post post = postRepository.findDerivativePostByPostId(postId);
        if(post != null){
            return mapToDTO(post);
        }else{
            return null;
        }
    }

    @Override
    public PostDTO getDerivativePostOfUser(int userId, int postId) {
        Post post = postRepository.findDerivativePostByUserId(userId, postId);
        if(post != null){
            return mapToDTO(post);
        }else{
            return null;
        }
    }

    @Override
    public PostDTO updatePost(GeneralPostDTO generalPostDTO, Post post, int userId,
                            List<String> imageLink) {
        UnitPrice unitPrice = unitPriceRepository.findById(generalPostDTO.getUnitPriceId())
                .orElseThrow(() -> new ResourceNotFoundException("UnitPrice", "id", generalPostDTO.getUnitPriceId()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (generalPostDTO.getDirectionId() == null) {
            post.setDirection(null);
        } else {
            Direction direction = directionRepository.findById(generalPostDTO.getDirectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Direction", "id", generalPostDTO.getDirectionId()));
            post.setDirection(direction);
        }
        if (generalPostDTO.getLongevityId() == null) {
            post.setLongevity(null);
        } else {
            Longevity longevity = longevityRepository.findById(generalPostDTO.getLongevityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Longevity", "id", generalPostDTO.getLongevityId()));
            post.setLongevity(longevity);
        }
        post.setUnitPrice(unitPrice);
        post.setUser(user);
        post.setTitle(generalPostDTO.getTitle());
        post.setDescription(generalPostDTO.getDescription());
        post.setArea(generalPostDTO.getArea());
        post.setCertification(generalPostDTO.isCertification());
        // unit name "thoa thuan" price null
        if (post.getUnitPrice().getId() == 3) {
            post.setPrice(null);
        } else {
            post.setPrice(generalPostDTO.getPrice());
        }
        post.setAdditionalDescription(generalPostDTO.getAdditionalDescription());
        post.setContactName(generalPostDTO.getContactName());
        post.setContactPhone(generalPostDTO.getContactPhone());
        post.setContactAddress(generalPostDTO.getContactAddress());
        post.setContactEmail(generalPostDTO.getContactEmail());
        post.setWard(generalPostDTO.getWard());
        post.setDistrict(generalPostDTO.getDistrict());
        post.setProvince(generalPostDTO.getProvince());
        post.setAddress(generalPostDTO.getAddress());
        if ((imageLink == null) || (imageLink.size() ==0)) {
            post.setThumbnail(null);
        } else {
            post.setThumbnail(imageLink.get(0));
        }
        Post newPost = postRepository.save(post);
        return mapToDTO(newPost);
    }

    @Override
    public void deletePost(int postId) {
        try {
            postRepository.deleteByPostId(postId);
        } catch (Exception e) {

        }

    }

    @Override
    public SearchResponse getAllPost(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> listPosts = posts.getContent();
        List<PostDTO> list = listPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listDto = new ArrayList<>();
        for (PostDTO postDto : list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listDto);
        searchResponse.setPageNo(pageNo + 1);
        searchResponse.setTotalPages(posts.getTotalPages());
        searchResponse.setTotalResult(posts.getTotalElements());
        return searchResponse;
    }

    @Override
    public SearchResponse getExpiredPostByUserId(int userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        long millis = System.currentTimeMillis();
//        java.sql.Date date = new java.sql.Date(millis);

        Page<Post> pagePost = postRepository.getExpiredPostByUserId(userId, pageable);
        List<Post> listPosts = pagePost.getContent();

        List<PostDTO> list = listPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listDto = new ArrayList<>();
        for (PostDTO postDto : list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listDto);
        searchResponse.setPageNo(pageNo + 1);
        searchResponse.setTotalPages(pagePost.getTotalPages());
        searchResponse.setTotalResult(pagePost.getTotalElements());
        return searchResponse;
    }


    @Override
    public SearchResponse getAllPost(int pageNo, int pageSize, String keyword, String sortValue) {
        int sortOption = Integer.parseInt(sortValue);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> posts = postRepository.findAll(pageable, keyword, sortOption);
        List<Post> listPosts = posts.getContent();
        List<PostDTO> list = listPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listDto = new ArrayList<>();
        for (PostDTO postDto : list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listDto);
        searchResponse.setPageNo(pageNo + 1);
        searchResponse.setTotalPages(posts.getTotalPages());
        searchResponse.setTotalResult(posts.getTotalElements());
        return searchResponse;
    }

    @Override
    public Map<String, List> getRealEstateHistory(int postId) {
        Post post = postRepository.findPostById(postId);
        int propertyType = post.getPropertyType().getId();
        Map<String, List> histories = new HashMap<>();
        if (propertyType == 1) {
            ResidentialHouse house = houseRepository.findByPostId(postId);
            String barcode = house.getBarcode();
            if(barcode != null){
                String historyBarcode = "";
                if (barcode.length() == 13) {
                    historyBarcode = barcode.substring(0, 5);
                }
                if (barcode.length() == 15) {
                    historyBarcode = barcode.substring(2, 7);
                }
                try{
                    List<ResidentialHouseHistory> houseHistories = houseHistoryRepository.getHouseHistoryByBarcodeAndPlotNumber(historyBarcode, house.getPlotNumber());
                    histories.remove("houseHistories");
                    if((houseHistories == null)){
                        histories.put("houseHistories", null);
                    }else{
                        histories.put("houseHistories", houseHistories);
                    }
                }catch (Exception e){
                    histories.put("houseHistories", null);
                }
            }else{
                histories.remove("houseHistories");
                histories.put("houseHistories", null);
            }

        }
        if (propertyType == 2) {
            Apartment apartment = apartmentRepository.findByPostId(postId);
            String barcode = apartment.getBarcode();
            if(barcode != null){
                String historyBarcode = "";
                if (barcode.length() == 13) {
                    historyBarcode = barcode.substring(0, 5);
                }
                if (barcode.length() == 15) {
                    historyBarcode = barcode.substring(2, 7);
                }
                try{
                    List<ApartmentHistory> apartmentHistories = apartmentHistoryRepository.getApartmentHistoryByBarcode(historyBarcode, apartment.getPlotNumber(), apartment.getBuildingName(), apartment.getRoomNumber());
                    histories.remove("apartmentHistories");
                    if((apartmentHistories == null)){
                        histories.put("apartmentHistories", null);
                    }else{
                        histories.put("apartmentHistories", apartmentHistories);
                    }
                }catch (Exception e){
                    histories.put("apartmentHistories", null);
                }
            }else{
                histories.remove("apartmentHistories");
                histories.put("apartmentHistories", null);
            }
        }
        if (propertyType == 3) {
            ResidentialLand land = landRepository.findByPostId(postId);
            String barcode = land.getBarcode();
            if(barcode != null){
                String historyBarcode = "";
                if (barcode.length() == 13) {
                    historyBarcode = barcode.substring(0, 5);
                }
                if (barcode.length() == 15) {
                    historyBarcode = barcode.substring(2, 7);
                }
                try{
                    List<ResidentialLandHistory> landHistories = landHistoryRepository.getLandHistoryByBarcodeAndPlotNumber(historyBarcode, land.getPlotNumber());
                    histories.remove("landHistories");
                    if((landHistories == null)){
                        histories.put("landHistories", null);
                    }else{
                        histories.put("landHistories", landHistories);
                    }
                }catch (Exception e){
                    histories.put("landHistories", null);
                }
            }else {
                histories.remove("landHistories");
                histories.put("landHistories", null);
            }

        }
        return histories;
    }

    @Override
    public List<DerivativeDTO> getAllDerivativePostPaging(int pageNo, int pageSize, String option) {
        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAll(pageable);
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllByPriceAsc(pageable);
                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllByPriceDesc(pageable);
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllByPricePerSquareAsc(pageable);
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findAllByPricePerSquareDesc(pageable);
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAll(pageable);
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAll(pageable);
                break;
        }

        List<Post> list = listPosts.getContent();
        return list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<DerivativeDTO> getAllDerivativePost() {
        List<Post> listPosts = postRepository.findAll();
        return listPosts.stream().map(derivativePost -> mapper.map(derivativePost, DerivativeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<DerivativeDTO> getDerivativePostByUserIdPaging(int userId, String propertyId, int pageNo, int pageSize, String option) {
        String check = null;
        int typeId = 0;
        if (propertyId != null) {
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
        List<DerivativeDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPriceAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
                return listDto;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPriceDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
                return listDto;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPricePerSquareAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
                return listDto;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPricePerSquareDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
                return listDto;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                break;
        }

        pageable = PageRequest.of(pageNo, pageSize, sort);
        listPosts = postRepository.getDerivativePostByUserId(userId, typeId, check, pageable);
        list = listPosts.getContent();
        listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
        return listDto;
    }

    @Override
    public SearchResponse getDerivativePostOfBrokerPaging(int userId, String propertyId, int pageNo, int pageSize, String option, String status) {
        String check = null;
        int typeId = 0;
        if (propertyId != null) {
            check = "";
            typeId = Integer.parseInt(propertyId);
        }

        int statusId = Integer.parseInt(status);
        int sortOption = Integer.parseInt(option);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findDerivativePostOfBroker(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPriceAsc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPriceDesc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPricePerSquareAsc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPricePerSquareDesc(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findDerivativePostOfBroker(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findDerivativePostOfBroker(userId, typeId, check, statusId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
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
            dto.setEndDate(postDto.getTransactionEndDate());
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
    public List<DerivativeDTO> getDerivativePostByUserId(int userId) {
        List<Post> listPosts = postRepository.getAllDerivativePostByUserId(userId);

        List<DerivativeDTO> listDto = listPosts.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());

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
        searchDTO.setStatus(postDTO.getStatus());
        searchDTO.setUnitPrice(postDTO.getUnitPrice());
        searchDTO.setThumbnail(postDTO.getThumbnail());
        searchDTO.setOriginalPost(postDTO.getOriginalPost());
        searchDTO.setAllowDerivative(postDTO.isAllowDerivative());
        UserDTO userDTO = postDTO.getUser();
        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userDTO.getId(), userDTO.getCurrentRole());
        if (avgRate != null) {
            userDTO.setAvgRate(avgRate.getAvgRate());
        } else {
            userDTO.setAvgRate(0);
        }
        searchDTO.setUser(userDTO);
        searchDTO.setBlock(postDTO.isBlock());

    }


    private PostDTO mapToDTO(Post post) {
        PostDTO postDTO = mapper.map(post, PostDTO.class);
        return postDTO;
    }

    private Post mapToEntity(PostDTO postDTO) {
        Post post = mapper.map(postDTO, Post.class);
        return post;
    }

    public PostDTO setDataToPostDTO(GeneralPostDTO generalPostDTO, int userId, Date date, boolean check) {
        PostDTO postDTO = new PostDTO();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        postDTO.setUser(mapper.map(user, UserDTO.class));
        postDTO.setTitle(generalPostDTO.getTitle());
        postDTO.setDescription(generalPostDTO.getDescription());
        postDTO.setArea(generalPostDTO.getArea());
        postDTO.setCertification(generalPostDTO.isCertification());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, generalPostDTO.getNumberOfPostedDay());
        postDTO.setStartDate(date);
        if (check == true) {
            postDTO.setTransactionStartDate(date);
            postDTO.setTransactionEndDate(c.getTime());
        } else {
            postDTO.setTransactionStartDate(null);
            postDTO.setTransactionEndDate(null);
        }
        postDTO.setAllowDerivative(true);

        // unit name "thoa thuan" price null
//        if (generalPostDTO.getUnitPriceId() == 3 || generalPostDTO.getPrice() < 0) {
//            postDTO.setPrice(null);
//        } else {
//            postDTO.setPrice(generalPostDTO.getPrice());
//        }
        postDTO.setAdditionalDescription(generalPostDTO.getAdditionalDescription());
        postDTO.setContactName(generalPostDTO.getContactName());
        postDTO.setContactPhone(generalPostDTO.getContactPhone());
        postDTO.setContactAddress(generalPostDTO.getContactAddress());
        postDTO.setContactEmail(generalPostDTO.getContactEmail());
        postDTO.setWard(generalPostDTO.getWard());
        postDTO.setDistrict(generalPostDTO.getDistrict());
        postDTO.setProvince(generalPostDTO.getProvince());
        postDTO.setAddress(generalPostDTO.getAddress());
        if ((generalPostDTO.getImages() != null) &&(generalPostDTO.getImages().size() != 0)) {
            postDTO.setThumbnail(generalPostDTO.getImages().get(0));
        } else {
            postDTO.setThumbnail(null);
        }
        postDTO.setOriginalPost(null);
        return postDTO;
    }


    // set information residential house from general post and create
    public ResidentialHouseDTO setDataToResidentialHouse(GeneralPostDTO generalPostDTO) {
        ResidentialHouseDTO residentialHouseDTO = new ResidentialHouseDTO();
        if (!generalPostDTO.isCertification()) {
            residentialHouseDTO.setBarcode(null);
            residentialHouseDTO.setPlotNumber(null);
            residentialHouseDTO.setOwner(null);
            residentialHouseDTO.setOwnerPhone(null);
        } else {
            residentialHouseDTO.setBarcode(generalPostDTO.getBarcode());
            residentialHouseDTO.setPlotNumber(generalPostDTO.getPlotNumber());
            residentialHouseDTO.setOwner(generalPostDTO.getOwner());
            residentialHouseDTO.setOwnerPhone(generalPostDTO.getOwnerPhone());
        }
        residentialHouseDTO.setNumberOfBedroom(generalPostDTO.getNumberOfBedroom());
        residentialHouseDTO.setNumberOfBathroom(generalPostDTO.getNumberOfBathroom());
        residentialHouseDTO.setNumberOfFloor(generalPostDTO.getNumberOfFloor());
        residentialHouseDTO.setFrontispiece(generalPostDTO.getFrontispiece());
        return residentialHouseDTO;
    }

    // set information residential house history from general post and create
    public ResidentialHouseHistoryDTO setDataToResidentialHouseHistory(GeneralPostDTO generalPostDTO, ResidentialHouseDTO residentialHouse, String date) {
        ResidentialHouseHistoryDTO residentialHouseHistoryDTO = new ResidentialHouseHistoryDTO();
        residentialHouseHistoryDTO.setBarcode(residentialHouse.getBarcode());
        residentialHouseHistoryDTO.setPlotNumber(residentialHouse.getPlotNumber());
        residentialHouseHistoryDTO.setOwner(generalPostDTO.getOwner());
        residentialHouseHistoryDTO.setStartDate(date);
        residentialHouseHistoryDTO.setPhone(generalPostDTO.getOwnerPhone());
        return residentialHouseHistoryDTO;
    }

    // set information apartment from general post and create
    public ApartmentDTO setDataToApartment(GeneralPostDTO generalPostDTO) {
        ApartmentDTO apartmentDTO = new ApartmentDTO();
        if (!generalPostDTO.isCertification()) {
            apartmentDTO.setBarcode(null);
            apartmentDTO.setBuildingName(null);
            apartmentDTO.setFloorNumber(null);
            apartmentDTO.setRoomNumber(null);
            apartmentDTO.setOwner(null);
            apartmentDTO.setOwnerPhone(null);
        } else {
            apartmentDTO.setPlotNumber(generalPostDTO.getPlotNumber());
            apartmentDTO.setBarcode(generalPostDTO.getBarcode());
            apartmentDTO.setBuildingName(generalPostDTO.getBuildingName());
            apartmentDTO.setFloorNumber(generalPostDTO.getFloorNumber());
            apartmentDTO.setRoomNumber(generalPostDTO.getRoomNumber());
            apartmentDTO.setOwner(generalPostDTO.getOwner());
            apartmentDTO.setOwnerPhone(generalPostDTO.getOwnerPhone());
        }
        apartmentDTO.setNumberOfBedroom(generalPostDTO.getNumberOfBedroom());
        apartmentDTO.setNumberOfBathroom(generalPostDTO.getNumberOfBathroom());
        return apartmentDTO;
    }

    // set information apartment history from general post and create
    public ApartmentHistoryDTO setDataToApartmentHistory(GeneralPostDTO generalPostDTO, ApartmentDTO apartmentDTO, String date) {
        ApartmentHistoryDTO apartmentHistoryDTO = new ApartmentHistoryDTO();
        apartmentHistoryDTO.setBarcode(apartmentDTO.getBarcode());
        apartmentHistoryDTO.setOwner(generalPostDTO.getOwner());
        apartmentHistoryDTO.setStartDate(date);
        apartmentHistoryDTO.setPhone(generalPostDTO.getOwnerPhone());

        return apartmentHistoryDTO;
    }

    // set information residential land from general post and create
    public ResidentialLandDTO setDataToResidentialLand(GeneralPostDTO generalPostDTO) {
        ResidentialLandDTO residentialLandDTO = new ResidentialLandDTO();
        if (!generalPostDTO.isCertification()) {
            residentialLandDTO.setBarcode(null);
            residentialLandDTO.setPlotNumber(null);
            residentialLandDTO.setOwner(null);
            residentialLandDTO.setOwnerPhone(null);
        } else {
            residentialLandDTO.setBarcode(generalPostDTO.getBarcode());
            residentialLandDTO.setPlotNumber(generalPostDTO.getPlotNumber());
            residentialLandDTO.setOwner(generalPostDTO.getOwner());
            residentialLandDTO.setOwnerPhone(generalPostDTO.getOwnerPhone());
        }
        residentialLandDTO.setFrontispiece(generalPostDTO.getFrontispiece());

        return residentialLandDTO;
    }

    // set information residential land history from general post and create
    public ResidentialLandHistoryDTO setDataToResidentialLandHistory(GeneralPostDTO generalPostDTO, ResidentialLandDTO residentialLandDTO, String date) {
        ResidentialLandHistoryDTO residentialLandHistoryDTO = new ResidentialLandHistoryDTO();
        residentialLandHistoryDTO.setBarcode(residentialLandDTO.getBarcode());
        residentialLandHistoryDTO.setPlotNumber(residentialLandDTO.getPlotNumber());
        residentialLandHistoryDTO.setOwner(generalPostDTO.getOwner());
        residentialLandHistoryDTO.setStartDate(date);
        residentialLandHistoryDTO.setPhone(generalPostDTO.getOwnerPhone());
        return residentialLandHistoryDTO;
    }

    public void setDataToRealEstateDTO(RealEstatePostDTO realEstatePostDTO, PostDTO postDTO, int postId) {
        UserDTO userDTO = postDTO.getUser();
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userDTO.getId()));
        Boolean isBroker = false;
        Set<Role> setRole = user.getRoles();
        Role role = roleRepository.findByName("BROKER").get();
        if(setRole.contains(role)){
            isBroker = true;
        }
        userDTO.setBroker(isBroker);
        realEstatePostDTO.setUser(userDTO);
        realEstatePostDTO.setPostId(postId);
        realEstatePostDTO.setArea(postDTO.getArea());
        realEstatePostDTO.setTitle(postDTO.getTitle());
        realEstatePostDTO.setDescription(postDTO.getDescription());
        realEstatePostDTO.setAddress(postDTO.getAddress());
        realEstatePostDTO.setCertification(postDTO.isCertification());
        Date date = postDTO.getStartDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        realEstatePostDTO.setStartDate(simpleDateFormat.format(date));
        if (postDTO.getTransactionStartDate() == null && postDTO.getTransactionEndDate() == null) {
            realEstatePostDTO.setTransactionStartDate(null);
            realEstatePostDTO.setTransactionEndDate(null);
        } else {
            realEstatePostDTO.setTransactionStartDate(simpleDateFormat.format(postDTO.getTransactionStartDate()));
            realEstatePostDTO.setTransactionEndDate(simpleDateFormat.format(postDTO.getTransactionEndDate()));
        }
        if (postDTO.getBlockDate() == null) {
            realEstatePostDTO.setBlockDate(null);
        } else {
            realEstatePostDTO.setBlockDate(simpleDateFormat.format(postDTO.getBlockDate()));
        }
        realEstatePostDTO.setBlock(postDTO.isBlock());
        realEstatePostDTO.setAdditionalDescription(postDTO.getAdditionalDescription());
        realEstatePostDTO.setPrice(postDTO.getPrice());
        realEstatePostDTO.setContactPhone(postDTO.getContactPhone());
        realEstatePostDTO.setContactName(postDTO.getContactName());
        realEstatePostDTO.setContactAddress(postDTO.getContactAddress());
        realEstatePostDTO.setContactEmail(postDTO.getContactEmail());
        realEstatePostDTO.setDistrict(postDTO.getDistrict());
        realEstatePostDTO.setWard(postDTO.getWard());
        realEstatePostDTO.setProvince(postDTO.getProvince());
        realEstatePostDTO.setAddress(postDTO.getAddress());
        realEstatePostDTO.setThumbnail(postDTO.getThumbnail());
        realEstatePostDTO.setUnitPrice(postDTO.getUnitPrice());
        realEstatePostDTO.setDirection(postDTO.getDirection());
        realEstatePostDTO.setLongevity(postDTO.getLongevity());
        realEstatePostDTO.setPropertyType(postDTO.getPropertyType());
        realEstatePostDTO.setStatus(postDTO.getStatus());
        realEstatePostDTO.setImages(postDTO.getImages());
        realEstatePostDTO.setCoordinates(postDTO.getCoordinates());
        realEstatePostDTO.setOriginalPost(postDTO.getOriginalPost());
        realEstatePostDTO.setAllowDerivative(postDTO.isAllowDerivative());
        realEstatePostDTO.setSpendMoney(postDTO.getSpendMoney());

    }

    @Override
    public int changeStatusOfPost(int postId) {
        Post post = postRepository.findPostById(postId);
        long millis = System.currentTimeMillis();
        Date sqlDate = new Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        Date date = new Date(c.getTimeInMillis());
        if (post != null) {
//            Status status = new Status();
            //khong phai bai da hoan thanh giao dich
            if (post.getStatus().getId() != 6) {
                if(post.isBlock()){
                    User user = post.getUser();
                    if(user.isBlock()){
                        return 0;
                    }
                    post.setBlock(false);
                    java.util.Date blockDate = post.getBlockDate();
                    post.setBlockDate(null);
                    postRepository.save(post);

//                    TextMessageDTO messageDTO = new TextMessageDTO();
                    String message = "Chúng tôi đã hiển thị lại bài viết của bạn, mã bài viết: " + postId;
//                    messageDTO.setMessage(message);
//                    template.convertAndSend("/topic/message/" + post.getUser().getId(), messageDTO);
                    Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                    pusher.setCluster("ap1");
                    pusher.setEncrypted(true);
                    pusher.trigger("my-channel-" + post.getUser().getId(), "my-event", Collections.singletonMap("message", message));

                    saveNotificationAndUpdateUser(message, post.getUser().getId(), postId, "OpenOriginalPostStatus");

                    List<Post> listPost = postRepository.getPostOfOriginalPost(postId);
                    for(Post p: listPost){
                        if(p.getBlockDate().compareTo(blockDate) == 0){
                            p.setBlock(false);
                            post.setBlockDate(null);
                            postRepository.save(p);

                            String message1 = "Chúng tôi đã hiển thị lại bài viết của bạn, mã bài viết: " + p.getPostId();
//                            messageDTO.setMessage(message1);
//                            template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);
                            pusher.setCluster("ap1");
                            pusher.setEncrypted(true);
                            pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message1));

                            saveNotificationAndUpdateUser(message1, p.getUser().getId(), p.getPostId(), "OpenDerivativePostStatus");
                        }
                    }
                }else{
                    post.setBlock(true);
                    post.setBlockDate(date);
                    postRepository.save(post);

                    TextMessageDTO messageDTO = new TextMessageDTO();
                    String message = "Chúng tôi đã ẩn bài viết mã số " + post.getPostId() + " của bạn. Nếu có thắc mắc xin liên hệ email rebroland@gmail.com";
//                    messageDTO.setMessage(message);
//                    template.convertAndSend("/topic/message/" + post.getUser().getId(), messageDTO);
                    Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                    pusher.setCluster("ap1");
                    pusher.setEncrypted(true);
                    pusher.trigger("my-channel-" + post.getUser().getId(), "my-event", Collections.singletonMap("message", message));

                    saveNotificationAndUpdateUser(message, post.getUser().getId(), post.getPostId(), "DropOriginalPostStatus");

                    List<Post> listPost = postRepository.getPostOfOriginalPost(postId);
                    for(Post p: listPost){
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

                return 1;
            } else {
                return 2;
            }
        } else {
            return 3;
        }
    }

    @Override
    public Map<String, Integer> getNumberOfPropertyType() {
        int numberOfHouse = postRepository.getNumberOfPropertyType(1);
        int numberOfApartment = postRepository.getNumberOfPropertyType(2);
        int numberOfLand = postRepository.getNumberOfPropertyType(3);
        Map<String, Integer> map = new HashMap<>();
        map.put("house", numberOfHouse);
        map.put("apartment", numberOfApartment);
        map.put("land", numberOfLand);
        return map;
    }

    @Override
    public Map<String, Integer> getNumberOfPropertyTypeForBroker(int userId) {
        int numberOfHouse = postRepository.getNumberOfPropertyTypeForBroker(1, userId);
        int numberOfApartment = postRepository.getNumberOfPropertyTypeForBroker(2, userId);
        int numberOfLand = postRepository.getNumberOfPropertyTypeForBroker(3, userId);
        Map<String, Integer> map = new HashMap<>();
        map.put("house", numberOfHouse);
        map.put("apartment", numberOfApartment);
        map.put("land", numberOfLand);
        return map;
    }

    @Override
    public void extendPost(int postId, int numberOfPostedDay, Long totalPayment) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        long millis = System.currentTimeMillis();
        Date dateNow = new Date(millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        calendar.add(Calendar.HOUR, 7);
        Date sqlDate = new Date(calendar.getTimeInMillis());

        Calendar c = Calendar.getInstance();
        c.setTime(dateNow);
        c.add(Calendar.DAY_OF_MONTH, numberOfPostedDay);
        post.setTransactionStartDate(sqlDate);
        post.setTransactionEndDate(c.getTime());
        Long oldPayment = post.getSpendMoney();
        post.setSpendMoney(oldPayment + totalPayment);
        Status status = statusRepository.findById(1).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        post.setStatus(status);
        postRepository.save(post);

        List<Post> listPost = postRepository.getDerivativePostOfOriginalPost(postId);
        if(listPost.size() != 0){
            for (Post p: listPost) {
                if(status.getId() == 1){
                    p.setBlock(false);
                    postRepository.save(p);

//                    TextMessageDTO messageDTO = new TextMessageDTO();
                    String message = "Bài viết gốc đã được gia hạn, chúng tôi hiển thị lại bài phái sinh của bạn!";
//                    messageDTO.setMessage(message);
//                    template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);
                    Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                    pusher.setCluster("ap1");
                    pusher.setEncrypted(true);
                    pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message));

                    saveNotificationAndUpdateUser(message, p.getUser().getId(), p.getPostId(), "Extend");
                }

            }
        }
    }


    @Override
    public void changeStatus(int postId, int statusId) {
        Status status = statusRepository.findById(statusId).orElseThrow(() -> new ResourceNotFoundException("Status", "id", statusId));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        post.setStatus(status);
        postRepository.save(post);

        List<Post> listPost = postRepository.getPostOfOriginalPost(postId);
        if(listPost.size() != 0){
            for (Post p: listPost) {
                if(statusId == 2 || statusId == 6){
                    p.setBlock(true);
                    postRepository.save(p);

//                    TextMessageDTO messageDTO = new TextMessageDTO();
                    String message = "Bài viết gốc bị gỡ, vì vậy chúng tôi sẽ đóng bài phái sinh của bạn!";
//                    messageDTO.setMessage(message);
//                    template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);
                    Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                    pusher.setCluster("ap1");
                    pusher.setEncrypted(true);
                    pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message));

                    saveNotificationAndUpdateUser(message, p.getUser().getId(), p.getPostId(), "DropDerivativePostStatus");
                }
                if(statusId == 1){
                    p.setBlock(false);
                    postRepository.save(p);

//                    TextMessageDTO messageDTO = new TextMessageDTO();
                    String message = "Bài viết gốc đã được hiển thị trở lại, chúng tôi hiển thị lại bài phái sinh của bạn!";
//                    messageDTO.setMessage(message);
//                    template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);
                    Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
                    pusher.setCluster("ap1");
                    pusher.setEncrypted(true);
                    pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message));

                    saveNotificationAndUpdateUser(message, p.getUser().getId(), p.getPostId(), "OpenDerivativePostStatus");
                }

            }
        }

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

    @Override
    public void changeStatusOfDerivativePostOfPost(int postId) {
        List<Post> listPost = postRepository.getDerivativePostOfOriginalPost(postId);
        for (Post post : listPost) {
            post.setStatus(new Status(3));
            postRepository.save(post);
        }
    }

    @Override
    public SearchResponse getAllOriginalPostByUserId(int userId, int pageNo, int pageSize, String option, String propertyType) {
        int sortOption = Integer.parseInt(option);
        int typeId = Integer.parseInt(propertyType);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllOriginalPostByUserId(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllOriginalPostByUserIdOrderByPriceAsc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllOriginalPostByUserIdOrderByPriceDesc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllOriginalPostByUserIdOrderByPricePerSquareAsc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getAllOriginalPostByUserIdOrderByPricePerSquareDesc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllOriginalPostByUserId(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllOriginalPostByUserId(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
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

//        List<Post> listPost = postRepository.getAllOriginalPostByUserId(userId);
//        return listPost.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
//
    }

    @Override
    public SearchResponse getOriginalPostByUserId(int userId, int pageNo, int pageSize, String option, String propertyType) {
        int sortOption = Integer.parseInt(option);
        int typeId = Integer.parseInt(propertyType);
        String sortOpt = "";
        String sortDir = "";
        Sort sort = null;
        Pageable pageable = null;
        Page<Post> listPosts = null;
        List<Post> list = null;
        List<PostDTO> listDto = null;
        switch (sortOption) {
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getOriginalPostByUserId(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getOriginalPostByUserIdOrderByPriceAsc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getOriginalPostByUserIdOrderByPriceDesc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getOriginalPostByUserIdOrderByPricePerSquareAsc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getOriginalPostByUserIdOrderByPricePerSquareDesc(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getOriginalPostByUserId(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                        Sort.by(sortOpt).ascending() : Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getOriginalPostByUserId(userId, typeId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
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
    public List<SearchDTO> getOutstandingPost() {
        List<Post> listPost = postRepository.getOutstandingPost();
        List<PostDTO> listDto = listPost.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
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
        return listSearchDto;
    }


}
