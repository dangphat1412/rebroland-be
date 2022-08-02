package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.*;
import vn.edu.fpt.rebroland.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private EntityManager entityManager;
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

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, DirectionRepository directionRepository,
                           PropertyTypeRepository propertyTypeRepository, UnitPriceRepository unitPriceRepository,
                           StatusRepository statusRepository, LongevityRepository longevityRepository, ModelMapper mapper,
                           ResidentialHouseRepository houseRepository, ResidentialHouseHistoryRepository houseHistoryRepository,
                           ApartmentRepository apartmentRepository, ApartmentHistoryRepository apartmentHistoryRepository,
                           ResidentialLandRepository landRepository, ResidentialLandHistoryRepository landHistoryRepository,
                           EntityManager entityManager) {

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
        this.entityManager = entityManager;
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
        Post newPost = postRepository.save(post);
        return mapToDTO(newPost);
    }

    @Override
    public SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId, String option) {
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getDerivativePostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPriceAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPriceDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPricePerSquareAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.getDerivativePostByUserIdOrderByPricePerSquareDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getDerivativePostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getDerivativePostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto: listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo+1);
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllPostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.getAllPostByUserId(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto: listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo+1);
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
        for (PostDTO postDto: list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listDto);
        searchResponse.setPageNo(pageNo+1);
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAllPostForBroker(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAllPostForBroker(pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto: listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo+1);
        searchResponse.setTotalPages(listPosts.getTotalPages());
        searchResponse.setTotalResult(listPosts.getTotalElements());
        return searchResponse;
    }

    @Override
    public List<BrokerInfoOfPostDTO> getDerivativePostOfOriginalPost(int originalPostId) {
        List<Post> listPost = postRepository.getDerivativePostOfOriginalPost(originalPostId);
        return listPost.stream().map(post -> mapper.map(post, BrokerInfoOfPostDTO.class)).collect(Collectors.toList());

    }


    @Override
    public SearchResponse searchPosts(String ward, String district, String province, String minPrice, String maxPrice,
                                     String minArea, String maxArea, List<String> propertyType, String keyword,
                                     List<String> direction, int bedroom, int pageNo, int pageSize, String option) {
        Long minP = null;
        if(minPrice != null){
            minP = Long.parseLong(minPrice);
        }

        Long maxP = 0L;
        if(maxPrice == null){
            maxP = Long.MAX_VALUE;
        }else{
            maxP = Long.parseLong(maxPrice);
        }

        Float maxA = 0f;
        if(maxArea == null){
            maxA = Float.MAX_VALUE;
        }else{
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 0, 0, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }
        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto: listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo+1);
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
        if(minPrice != null){
            minP = Long.parseLong(minPrice);
        }

        Long maxP = 0L;
        if(maxPrice == null){
            maxP = Long.MAX_VALUE;
        }else{
            maxP = Long.parseLong(maxPrice);
        }

        Float maxA = 0f;
        if(maxArea == null){
            maxA = Float.MAX_VALUE;
        }else{
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.searchPosts(ward, district, province, minP, maxP,
                        minA, maxA, listType, keyword, check, listId, bedroom, 1, userId, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }
        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto: listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo+1);
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
        return mapToDTO(post);
    }

    @Override
    public PostDTO updatePost(PostDTO postDTO, int postId, int userId,
                              Integer directionId, int propertyTypeId,
                              int unitId, int statusId, Integer longevityId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        Direction direction = directionRepository.findById(directionId)
                .orElseThrow(() -> new ResourceNotFoundException("Direction", "id", directionId));
        PropertyType propertyType = propertyTypeRepository.findById(propertyTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyType", "id", propertyTypeId));
        UnitPrice unitPrice = unitPriceRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("UnitPrice", "id", unitId));
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", statusId));
        Longevity longevity = longevityRepository.findById(longevityId)
                .orElseThrow(() -> new ResourceNotFoundException("Longevity", "id", longevityId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (directionId == null) {
            post.setUnitPrice(null);
        } else {
            post.setUnitPrice(unitPrice);
        }

        if (longevityId == null) {
            post.setLongevity(null);
        } else {
            post.setLongevity(longevity);
        }
        post.setDirection(direction);
        post.setPropertyType(propertyType);
        post.setUnitPrice(unitPrice);
        post.setStatus(status);
        post.setUser(user);

        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setArea(postDTO.getArea());
        post.setCertification(postDTO.isCertification());
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        post.setStartDate(date);
        // unit name "thoa thuan" price null
        if (post.getUnitPrice().getId() == 3 || postDTO.getPrice() < 0) {
            post.setPrice(null);
        } else {
            post.setPrice(postDTO.getPrice());
        }
        post.setAdditionalDescription(postDTO.getAdditionalDescription());
        post.setContactName(postDTO.getContactName());
        post.setContactPhone(postDTO.getContactPhone());
        post.setContactAddress(postDTO.getContactAddress());
        post.setContactEmail(postDTO.getContactEmail());
        post.setWard(postDTO.getWard());
        post.setDistrict(postDTO.getDistrict());
        post.setProvince(postDTO.getProvince());
        post.setAddress(postDTO.getAddress());
//        post.setParentId(postDTO.getParentId());
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
        String sortByStartDate = "startDate";
        String sortDir = "desc";
        Sort sortStartDate = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortByStartDate).ascending(): Sort.by(sortByStartDate).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortStartDate);
        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> listPosts = posts.getContent();
        List<PostDTO> list = listPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        List<SearchDTO> listDto = new ArrayList<>();
        for (PostDTO postDto: list) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listDto);
        searchResponse.setPageNo(pageNo+1);
        searchResponse.setTotalPages(posts.getTotalPages());
        return searchResponse;
    }

    @Override
    public Map<String, List> getRealEstateHistory(int postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        int propertyType = post.getPropertyType().getId();
        Map<String, List> histories = new HashMap<>();
        if(propertyType == 1){
            ResidentialHouse house = houseRepository.findByPostId(postId);
            String barcode = house.getBarcode();
            String historyBarcode = "" ;
            if(barcode.length() == 13){
                historyBarcode = barcode.substring(0, 5);
            }
            if(barcode.length() == 15){
                historyBarcode = barcode.substring(0, 7);
            }
            List<ResidentialHouseHistory> houseHistories = houseHistoryRepository.getHouseHistoryByBarcodeAndPlotNumber(historyBarcode, house.getPlotNumber());
            histories.remove("houseHistories");
            histories.put("houseHistories", houseHistories);
        }
        if(propertyType == 2){
            Apartment apartment = apartmentRepository.findByPostId(postId);
            String barcode = apartment.getBarcode();
            String historyBarcode = "" ;
            if(barcode.length() == 13){
                historyBarcode = barcode.substring(0, 5);
            }
            if(barcode.length() == 15){
                historyBarcode = barcode.substring(0, 7);
            }
            List<ApartmentHistory> apartmentHistories = apartmentHistoryRepository.getApartmentHistoryByBarcode(historyBarcode, apartment.getPlotNumber(), apartment.getBuildingName(), apartment.getRoomNumber());
            histories.remove("apartmentHistories");
            histories.put("apartmentHistories", apartmentHistories);
        }
        if(propertyType == 3){
            ResidentialLand land = landRepository.findByPostId(postId);
            String barcode = land.getBarcode();
            String historyBarcode = "" ;
            if(barcode.length() == 13){
                historyBarcode = barcode.substring(0, 5);
            }
            if(barcode.length() == 15){
                historyBarcode = barcode.substring(0, 7);
            }
            List<ResidentialLandHistory> landHistories = landHistoryRepository.getLandHistoryByBarcodeAndPlotNumber(historyBarcode, land.getPlotNumber());
            histories.remove("landHistories");
            histories.put("landHistories", landHistories);
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findAll(pageable);
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
        List<DerivativeDTO> listDto = null;
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
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
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                break;
        }

        pageable = PageRequest.of(pageNo, pageSize, sort);
        listPosts = postRepository.getDerivativePostByUserId(userId, typeId, check, pageable);
        list = listPosts.getContent();
        listDto = list.stream().map(post -> mapper.map(post, DerivativeDTO.class)).collect(Collectors.toList());
        return listDto;
    }

    @Override
    public SearchResponse getDerivativePostOfBrokerPaging(int userId, String propertyId, int pageNo, int pageSize, String option) {
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
        switch (sortOption){
            case 0:
                sortOpt = "start_date";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findDerivativePostOfBroker(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 1:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPriceAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

                break;
            case 2:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPriceDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 3:
                //giá trên m2 từ thấp đến cao
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPricePerSquareAsc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 4:
                pageable = PageRequest.of(pageNo, pageSize);
                listPosts = postRepository.findDerivativePostOfBrokerOrderByPricePerSquareDesc(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 5:
                sortOpt = "area";
                sortDir = "asc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findDerivativePostOfBroker(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
            case 6:
                sortOpt = "area";
                sortDir = "desc";
                sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                        Sort.by(sortOpt).ascending(): Sort.by(sortOpt).descending();
                pageable = PageRequest.of(pageNo, pageSize, sort);
                listPosts = postRepository.findDerivativePostOfBroker(userId, typeId, check, pageable);
                list = listPosts.getContent();
                listDto = list.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
                break;
        }

        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (PostDTO postDto: listDto) {
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDto);
            listSearchDto.add(dto);
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNo+1);
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

        if(postDTO.getPrice() !=  null){
            searchDTO.setPrice(postDTO.getPrice());
        }else{
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
        searchDTO.setUser(postDTO.getUser());
    }


    private PostDTO mapToDTO(Post post) {
        PostDTO postDTO = mapper.map(post, PostDTO.class);
        return postDTO;
    }

    private Post mapToEntity(PostDTO postDTO) {
        Post post = mapper.map(postDTO, Post.class);
        return post;
    }

    public PostDTO setDataToPostDTO(GeneralPostDTO generalPostDTO, int userId, Date date) {
        PostDTO postDTO = new PostDTO();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        postDTO.setUser(mapper.map(user, UserDTO.class));
        postDTO.setTitle(generalPostDTO.getTitle());
        postDTO.setDescription(generalPostDTO.getDescription());
        postDTO.setArea(generalPostDTO.getArea());
        postDTO.setCertification(generalPostDTO.isCertification());

        postDTO.setStartDate(date);
        // unit name "thoa thuan" price null
        if (generalPostDTO.getUnitPriceId() == 3 || generalPostDTO.getPrice() < 0) {
            postDTO.setPrice(null);
        } else {
            postDTO.setPrice(generalPostDTO.getPrice());
        }
        postDTO.setAdditionalDescription(generalPostDTO.getAdditionalDescription());
        postDTO.setContactName(generalPostDTO.getContactName());
        postDTO.setContactPhone(generalPostDTO.getContactPhone());
        postDTO.setContactAddress(generalPostDTO.getContactAddress());
        postDTO.setContactEmail(generalPostDTO.getContactEmail());
        postDTO.setWard(generalPostDTO.getWard());
        postDTO.setDistrict(generalPostDTO.getDistrict());
        postDTO.setProvince(generalPostDTO.getProvince());
        postDTO.setAddress(generalPostDTO.getAddress());
        postDTO.setThumbnail(generalPostDTO.getImages().get(1));
        postDTO.setOriginalPost(null);
        return postDTO;
    }

    // set information residential house from general post and create
    public ResidentialHouseDTO setDataToResidentialHouse(GeneralPostDTO generalPostDTO) {
        ResidentialHouseDTO residentialHouseDTO = new ResidentialHouseDTO();
        if(!generalPostDTO.isCertification()){
            residentialHouseDTO.setBarcode(null);
            residentialHouseDTO.setPlotNumber(null);
            residentialHouseDTO.setOwner(null);
            residentialHouseDTO.setOwnerPhone(null);
        }else{
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
        if(!generalPostDTO.isCertification()){
            apartmentDTO.setBarcode(null);
            apartmentDTO.setBuildingName(null);
            apartmentDTO.setFloorNumber(null);
            apartmentDTO.setRoomNumber(null);
            apartmentDTO.setOwner(null);
            apartmentDTO.setOwnerPhone(null);
        }else{
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
        if(!generalPostDTO.isCertification()){
            residentialLandDTO.setBarcode(null);
            residentialLandDTO.setPlotNumber(null);
            residentialLandDTO.setOwner(null);
            residentialLandDTO.setOwnerPhone(null);
        }else{
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
        realEstatePostDTO.setUser(postDTO.getUser());
        realEstatePostDTO.setPostId(postId);
        realEstatePostDTO.setArea(postDTO.getArea());
        realEstatePostDTO.setTitle(postDTO.getTitle());
        realEstatePostDTO.setDescription(postDTO.getDescription());
        realEstatePostDTO.setAddress(postDTO.getAddress());
        realEstatePostDTO.setCertification(postDTO.isCertification());
        Date date = postDTO.getStartDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        realEstatePostDTO.setStartDate(simpleDateFormat.format(date));

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

    }



}
