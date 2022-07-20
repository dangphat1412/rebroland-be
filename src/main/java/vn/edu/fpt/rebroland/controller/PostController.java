package vn.edu.fpt.rebroland.controller;


import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/posts")
public class PostController {

    private PostService postService;
    private CoordinateService coordinateService;
    private ImageService imageService;
    private ApartmentService apartmentService;
    private ResidentialLandService residentialLandService;
    private ResidentialHouseService residentialHouseService;
    private ApartmentHistoryService apartmentHistoryService;
    private ResidentialHouseHistoryService residentialHouseHistoryService;
    private ResidentialLandHistoryService residentialLandHistoryService;
    private PostRepository postRepository;
    private UserRepository userRepository;
    private ModelMapper mapper;
    private UserFollowPostService userFollowPostService;


    public PostController(PostService postService, CoordinateService coordinateService, ImageService imageService,
                          ApartmentService apartmentService, ResidentialLandService residentialLandService,
                          ResidentialHouseService residentialHouseService, ApartmentHistoryService apartmentHistoryService,
                          ResidentialHouseHistoryService residentialHouseHistoryService, ResidentialLandHistoryService residentialLandHistoryService,
                          UserRepository userRepository, ModelMapper mapper,
                          UserFollowPostService userFollowPostService, PostRepository postRepository) {
        this.postService = postService;
        this.coordinateService = coordinateService;
        this.imageService = imageService;
        this.apartmentService = apartmentService;
        this.residentialLandService = residentialLandService;
        this.residentialHouseService = residentialHouseService;
        this.apartmentHistoryService = apartmentHistoryService;
        this.residentialHouseHistoryService = residentialHouseHistoryService;
        this.residentialLandHistoryService = residentialLandHistoryService;
        this.userRepository = userRepository;
        this.userFollowPostService = userFollowPostService;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }


    //view detail real estate post from post id
    @GetMapping("/{postId}")
    public ResponseEntity<RealEstatePostDTO> getDetailPost(@PathVariable int postId) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        PostDTO postDTO = postService.getPostByPostId(postId);
        switch (postDTO.getPropertyType().getId()) {
            case 1: // view residential house
                ResidentialHouseDTO residentialHouseDTO = residentialHouseService.getResidentialHouseByPostId(postId);
                realEstatePostDTO = residentialHouseDTO;
                break;
            case 2:// view apartment
                ApartmentDTO apartmentDTO = apartmentService.getApartmentByPostId(postId);
                realEstatePostDTO = apartmentDTO;
                break;
            case 3:// view residential land
                ResidentialLandDTO residentialLandDTO = residentialLandService.getResidentialLandByPostId(postId);
                realEstatePostDTO = residentialLandDTO;
                break;
        }
        postService.setDataToRealEstateDTO(realEstatePostDTO, postDTO, postId);
        return new ResponseEntity<>(realEstatePostDTO, HttpStatus.OK);
    }




   //  create derivative post
    @PostMapping("derivative/{id}")
    @Transactional
    public ResponseEntity<String> createDerivativePost(@PathVariable(name = "id") int postId,
                                                       @Valid @RequestBody GeneralPostDTO generalPostDTO,
                                                       @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Post post = postRepository.findPostByPostId(postId);
        if (user.getCurrentRole() == 3) {
            if (user.getId() != post.getUser().getId()) {
                PostDTO postDTO = postService.setDataToPostDTO(generalPostDTO, userId, date);
                postDTO.setOriginalPost(postId);
                PostDTO newPostDTO = postService.createPost(postDTO, userId, generalPostDTO.getDirectionId(), generalPostDTO.getPropertyTypeId(),
                        generalPostDTO.getUnitPriceId(), 2, generalPostDTO.getLongevityId());
                imageService.createImage(generalPostDTO.getImages(), newPostDTO.getPostId());
                coordinateService.createCoordinate(generalPostDTO.getCoordinates(), newPostDTO.getPostId());
                switch (generalPostDTO.getPropertyTypeId()) {
                    case 1:
                        ResidentialHouseDTO newResidentialHouse = postService.setDataToResidentialHouse(generalPostDTO);
                        residentialHouseService.createResidentialHouse(newResidentialHouse, newPostDTO.getPostId());

                        break;
                    case 2:
                        ApartmentDTO newApartmentDTO = postService.setDataToApartment(generalPostDTO);
                        apartmentService.createApartment(newApartmentDTO, newPostDTO.getPostId());
                        break;
                    case 3:
                        ResidentialLandDTO newResidentialLand = postService.setDataToResidentialLand(generalPostDTO);
                        residentialLandService.createResidentialLand(newResidentialLand, newPostDTO.getPostId());
                        break;
                }
            } else {
                return new ResponseEntity<>("You can not create derivative post with your original post", HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>("You need change to be broker", HttpStatus.CREATED);
        }

        return new ResponseEntity<>(" Derivative Post success", HttpStatus.CREATED);
    }


    // create general post
    @PostMapping(consumes = "*/*")
    @Transactional
    public ResponseEntity<String> createPost(@Valid @RequestBody GeneralPostDTO generalPostDTO,
                                             @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//        String startDate = "";
//        if (generalPostDTO.getBarcode().length() == 13) {
//            startDate = "20" + generalPostDTO.getBarcode().substring(5, 7);
//        } else {
//            startDate = "20" + generalPostDTO.getBarcode().substring(7, 9);
//        }
        if (user.getCurrentRole() == 2) {
            PostDTO postDTO = postService.setDataToPostDTO(generalPostDTO, userId, date);
            PostDTO newPostDTO = postService.createPost(postDTO, userId, generalPostDTO.getDirectionId(), generalPostDTO.getPropertyTypeId(),
                    generalPostDTO.getUnitPriceId(), 2, generalPostDTO.getLongevityId());
            imageService.createImage(generalPostDTO.getImages(), newPostDTO.getPostId());
            coordinateService.createCoordinate(generalPostDTO.getCoordinates(), newPostDTO.getPostId());
            if (generalPostDTO.getPropertyTypeId() == 1) {
                ResidentialHouseDTO newResidentialHouse = postService.setDataToResidentialHouse(generalPostDTO);
                residentialHouseService.createResidentialHouse(newResidentialHouse, newPostDTO.getPostId());
                return new ResponseEntity<>(" House success", HttpStatus.CREATED);
            }
            if (generalPostDTO.getPropertyTypeId() == 2) {
                ApartmentDTO newApartmentDTO = postService.setDataToApartment(generalPostDTO);
                apartmentService.createApartment(newApartmentDTO, newPostDTO.getPostId());
                return new ResponseEntity<>("Apartment success", HttpStatus.CREATED);
            }
            if (generalPostDTO.getPropertyTypeId() == 3) {
                ResidentialLandDTO newResidentialLand = postService.setDataToResidentialLand(generalPostDTO);
                residentialLandService.createResidentialLand(newResidentialLand, newPostDTO.getPostId());
                return new ResponseEntity<>("Land success", HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>("You need change to customer", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("fail", HttpStatus.CREATED);
    }

    //get post of user
    @GetMapping("/user")
    public SearchResponse getPostByUserToken(@RequestHeader(name = "Authorization") String token,
                                             @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                             @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                             @RequestParam(name = "sortValue", defaultValue = "0") String sortValue) {
        int userId = getUserIdFromToken(token);
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        return postService.getPostByUserId(pageNumber, pageSize, userId, propertyTypeId, sortValue);
    }

    //get all post for broker
    @GetMapping("/broker")
    public ResponseEntity<?> getAllPost(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo) {
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;

        SearchResponse searchResponse = postService.getAllPost(pageNumber, pageSize);

        return new ResponseEntity<>(searchResponse, HttpStatus.OK);
    }

    // decode token
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    // get userid from token
    private int getUserIdFromToken(String token) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        return user.getId();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "postId") int postId,
                                             @RequestHeader(name = "Authorization") String token) {

        PostDTO postDTO = postService.getPostByPostId(postId);
        int userId = getUserIdFromToken(token);
        String result = "";
        if (userId == postDTO.getUser().getId()) {
            switch (postDTO.getPropertyType().getId()) {
                case 1:
                    residentialHouseService.deleteResidentialHouseByPostId(postId);
                    result = "delete house successfull";
                    break;
                case 2:
                    apartmentService.deleteApartmentByPostId(postId);
                    result = "delete apartment successfull";
                    break;
                case 3:
                    residentialLandService.deleteResidentialLandByPostId(postId);
                    result = "delete land successfull";
                    break;
            }
            coordinateService.deleteCoordinateByPostId(postId);
            imageService.deleteImageByPostId(postId);
            postService.deletePost(postId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("Delete fail", HttpStatus.OK);
    }

    //search
    @GetMapping
    public ResponseEntity<?> getPostByAttribute(@RequestParam(name = "ward", defaultValue = "") String ward,
                                                @RequestParam(name = "district", defaultValue = "") String district,
                                                @RequestParam(name = "province", defaultValue = "") String province,
                                                @RequestParam(name = "minPrice", defaultValue = "0") String minPrice,
                                                @RequestParam(name = "maxPrice", required = false) String maxPrice,
                                                @RequestParam(name = "minArea", defaultValue = "0") String minArea,
                                                @RequestParam(name = "maxArea", required = false) String maxArea,
                                                @RequestParam(name = "propertyType", defaultValue = "1,2,3") List<String> listPropertyType,
                                                @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                @RequestParam(name = "direction", required = false) List<String> listDirectionId,
                                                @RequestParam(name = "numberOfBedroom", defaultValue = "0") String numberOfBedroom,
                                                @RequestParam(name = "pageNo", defaultValue = "0") String pageNo
    ) {
        try {
            int numberBedroom = Integer.parseInt(numberOfBedroom);
            int pageSize = 5;
            int pageNumber = Integer.parseInt(pageNo);

            SearchResponse list = postService.searchPosts(ward, district, province, minPrice, maxPrice,
                    minArea, maxArea, listPropertyType, keyword, listDirectionId, numberBedroom,
                    pageNumber, pageSize);
            if (list.getPosts().size() == 0) {
                return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Đã xảy ra lỗi!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{postId}")
    @Transactional
    public ResponseEntity<String> updatePost(@PathVariable int postId,
                                             @Valid @RequestBody GeneralPostDTO generalPostDTO,
                                             @RequestHeader(name = "Authorization") String token) {
        long millis = System.currentTimeMillis();
        java.sql.Date dateNow = new java.sql.Date(millis);
        int userId = getUserIdFromToken(token);

        PostDTO postDTO = postService.getPostByPostId(postId);
        int propertyId = postDTO.getPropertyType().getId();
        Long date = postDTO.getStartDate().getTime();
        postDTO = postService.setDataToPostDTO(generalPostDTO, userId, dateNow);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String startDate = "";
//        if (generalPostDTO.getBarcode().length() == 13) {
//            startDate = "20" + generalPostDTO.getBarcode().substring(5, 7);
//        } else {
//            startDate = "20" + generalPostDTO.getBarcode().substring(7, 9);
//        }
        if (userId == postDTO.getUser().getId()) {
            PostDTO newPostDTO = postService.updatePost(postDTO, postId, userId, generalPostDTO.getDirectionId(),
                    generalPostDTO.getPropertyTypeId(), generalPostDTO.getUnitPriceId(), 1, generalPostDTO.getLongevityId());
            switch (propertyId) {
                case 1:
                    ResidentialHouseDTO oldResidentialHouseDTO = residentialHouseService.getResidentialHouseByPostId(postId);
                    ResidentialHouseDTO newResidentialHouseDTO = postService.setDataToResidentialHouse(generalPostDTO);
                    residentialHouseService.updateResidentialHouse(newResidentialHouseDTO, postId, oldResidentialHouseDTO.getId());
                    break;
                case 2:
                    ApartmentDTO oldApartmentDTO = apartmentService.getApartmentByPostId(postId);
                    ApartmentDTO newApartmentDTO = postService.setDataToApartment(generalPostDTO);
                    apartmentService.updateApartment(newApartmentDTO, postId, oldApartmentDTO.getId());
//
                    break;
                case 3:
                    ResidentialLandDTO oldResidentialLandDTO = residentialLandService.getResidentialLandByPostId(postId);
                    ResidentialLandDTO newResidentialLandDTO = postService.setDataToResidentialLand(generalPostDTO);
                    residentialLandService.updateResidentialLand(newResidentialLandDTO, postId, oldResidentialLandDTO.getId());
                    break;

            }
            imageService.updateImage(generalPostDTO.getImages(), postId);
            coordinateService.updateCoordinate(generalPostDTO.getCoordinates(), postId);
            return new ResponseEntity<>("update success", HttpStatus.OK);
        }


        return new ResponseEntity<>("update fail", HttpStatus.OK);
    }

    //user follow post
    @PostMapping("/follow/{postId}")
    public ResponseEntity<?> createUserFollowPost(@PathVariable(name = "postId", required = false) String postId,
                                                  @RequestHeader(name = "Authorization") String token){

        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");

        HttpStatus follow = userFollowPostService.createUserFollowPost(postId, phone);
        return new ResponseEntity<>(follow);
    }

    //get follow post of user or broker
    @GetMapping("/user/follow")
    public ResponseEntity<?> getFollowPostByUser(@RequestHeader(name = "Authorization") String token,
                                                 @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                 @RequestParam(name = "propertyType", required = false) String propertyTypeId){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        List<DerivativeDTO> list = userFollowPostService.getFollowPostByUserPaging(phone, propertyTypeId, pageNumber, pageSize);

        List<DerivativeDTO> listAll = userFollowPostService.getFollowPostByUser(phone);

        int totalPage = 0;
        if (listAll.size() % pageSize == 0) {
            totalPage = listAll.size() / pageSize;
        } else {
            totalPage = (listAll.size() / pageSize) + 1;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("posts", list);
        map.put("totalResult", listAll.size());
        map.put("pageNo", pageNumber + 1);
        map.put("totalPages", totalPage);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/user/follow/short")
    public ResponseEntity<?> getTitleFollowPostOfUser(@RequestHeader(name = "Authorization") String token){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");

        List<ShortPostDTO> list = userFollowPostService.getShortFollowPostByUser(phone);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/history/{postId}")
    public ResponseEntity<?> getRealEstateHistory(@PathVariable(name = "postId") int postId){

        Map<String, List> history = postService.getRealEstateHistory(postId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    //list all post and derivative_posts
    @GetMapping("/lists")
    public ResponseEntity<?> getListPostAndDerivativePost(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        List<DerivativeDTO> lists = postService.getAllDerivativePostPaging(pageNumber, pageSize);

        List<DerivativeDTO> listAll = postService.getAllDerivativePost();

        int totalPage = 0;
        if (listAll.size() % pageSize == 0) {
            totalPage = listAll.size() / pageSize;
        } else {
            totalPage = (listAll.size() / pageSize) + 1;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("posts", lists);
        map.put("totalResult", listAll.size());
        map.put("pageNo", pageNumber + 1);
        map.put("totalPages", totalPage);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //get list derivative post of broker
    @GetMapping("/broker/list")
    public ResponseEntity<?> getListDerivativePostByUserId(@RequestHeader(name = "Authorization") String token,
                                                           @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                           @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                                           @RequestParam(name = "sortValue", defaultValue = "0") String sortValue
    ) {
        try{
            int userId = getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            if(user.getCurrentRole() != 3){
                return new ResponseEntity<>("Người dùng không phải là broker!", HttpStatus.OK);
            }
            int pageNumber = Integer.parseInt(pageNo);
            int pageSize = 8;

            List<DerivativeDTO> lists = postService.getDerivativePostByUserIdPaging(userId, propertyTypeId, pageNumber, pageSize, sortValue);

            List<DerivativeDTO> listAll = postService.getDerivativePostByUserId(userId);

            int totalPage = 0;
            if (listAll.size() % pageSize == 0) {
                totalPage = listAll.size() / pageSize;
            } else {
                totalPage = (listAll.size() / pageSize) + 1;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("posts", lists);
            map.put("pageNo", pageNumber + 1);
            map.put("totalResult", listAll.size());
            map.put("totalPages", totalPage);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Trang này không tồn tại!", HttpStatus.OK);
        }
    }

    //get list post by user id
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getListPostByUserId(@PathVariable(name = "userId") String id,
                                             @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                            @RequestParam(name = "pageNo", defaultValue = "0") String pageNo) {
        int userId = Integer.parseInt(id);
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 8;

        SearchResponse searchResponse = postService.getPostByUserId(pageNumber, pageSize, userId, propertyTypeId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserDTO userDTO = mapper.map(user, UserDTO.class);

        Map<String, Object> map = new HashMap<>();
        map.put("lists", searchResponse);
        map.put("user", userDTO);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //get list post for broker choose
    @GetMapping("/broker/original")
    public ResponseEntity<?> getListPostForBroker(@RequestHeader(name = "Authorization") String token,
                                                 @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                 @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                                 @RequestParam(name = "sortValue", defaultValue = "0") String sortValue
    ) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if(user.getCurrentRole() == 3){
            int pageNumber = Integer.parseInt(pageNo);
            int pageSize = 8;
            SearchResponse searchResponse = postService.getAllPostForBroker(pageNumber,pageSize, propertyTypeId);
            return new ResponseEntity<>(searchResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Người dùng không phải là broker !", HttpStatus.OK);
        }



    }
}
