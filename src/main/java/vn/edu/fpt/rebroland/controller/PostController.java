package vn.edu.fpt.rebroland.controller;


import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.*;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "https://rebroland.vercel.app")
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
    private AvgRateRepository rateRepository;
    private ModelMapper mapper;
    private UserFollowPostService userFollowPostService;
    private ReportService reportService;
    private ContactService contactService;
    private UserCareService userCareService;
    private PriceService priceService;
    private HistoryImageService historyImageService;
    private PaymentService paymentService;
    private RoleRepository roleRepository;
    private NotificationService notificationService;
    private RefundPercentService refundPercentService;

    public PostController(PostService postService, CoordinateService coordinateService, ImageService imageService,
                          ApartmentService apartmentService, ResidentialLandService residentialLandService,
                          ResidentialHouseService residentialHouseService, ApartmentHistoryService apartmentHistoryService,
                          ResidentialHouseHistoryService residentialHouseHistoryService, ResidentialLandHistoryService residentialLandHistoryService,
                          PostRepository postRepository, UserRepository userRepository, ModelMapper mapper,
                          UserFollowPostService userFollowPostService, ReportService reportService, ContactService contactService,
                          UserCareService userCareService, AvgRateRepository rateRepository, PriceService priceService, HistoryImageService historyImageService,
                          PaymentService paymentService, RoleRepository roleRepository, NotificationService notificationService,
                          RefundPercentService refundPercentService) {
        this.postService = postService;
        this.coordinateService = coordinateService;
        this.imageService = imageService;
        this.apartmentService = apartmentService;
        this.residentialLandService = residentialLandService;
        this.residentialHouseService = residentialHouseService;
        this.apartmentHistoryService = apartmentHistoryService;
        this.residentialHouseHistoryService = residentialHouseHistoryService;
        this.residentialLandHistoryService = residentialLandHistoryService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userFollowPostService = userFollowPostService;
        this.reportService = reportService;
        this.contactService = contactService;
        this.userCareService = userCareService;
        this.rateRepository = rateRepository;
        this.priceService = priceService;
        this.historyImageService = historyImageService;
        this.paymentService = paymentService;
        this.roleRepository = roleRepository;
        this.notificationService = notificationService;
        this.refundPercentService = refundPercentService;
    }

    //view detail real estate post from post id
    @GetMapping("/{postId}")
    public ResponseEntity<?> getDetailPost(@PathVariable int postId) {
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
        List<BrokerInfoOfPostDTO> list = postService.getDerivativePostOfOriginalPost(postId);
        Map<String, Object> map = new HashMap<>();
        map.put("post", realEstatePostDTO);
        map.put("brokers", list);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //  create derivative post
    @PostMapping("/derivative/{id}")
    @Transactional
    public ResponseEntity<String> createDerivativePost(@PathVariable(name = "id") int postId,
                                                       @Valid @RequestBody GeneralPostDTO generalPostDTO,
                                                       @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Post post = postRepository.findPostByPostId(postId);
        Post checkPost = postRepository.getPostByUserIdAndOriginalId(postId, userId);
        if (checkPost != null) {
            return new ResponseEntity<>("Bạn đã tạo bài phái sinh cho bài viết này", HttpStatus.BAD_REQUEST);
        }

        if (post.isAllowDerivative() && post.getOriginalPost() == null) {
            if (user.getCurrentRole() == 3) {
                if (user.getId() != post.getUser().getId()) {
                    Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
                    if (pattern.matcher(generalPostDTO.getContactName()).find()) {
                        return new ResponseEntity<>("Tên liên lạc không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                    }
                    if (generalPostDTO.getOwner() != null) {
                        if (pattern.matcher(generalPostDTO.getOwner()).find()) {
                            return new ResponseEntity<>("Tên chủ hộ không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                        }
                    }
                    PostDTO postDTO = postService.setDataToPostDTO(generalPostDTO, userId, date, false);
                    postDTO.setOriginalPost(postId);
                    postDTO.setTransactionStartDate(post.getTransactionStartDate());
                    postDTO.setTransactionEndDate(post.getTransactionEndDate());
                    if(generalPostDTO.getUnitPriceId() ==3 ){
                        postDTO.setPrice(null);
                    }else {
                        if(generalPostDTO.getPrice()==null){
                            return new ResponseEntity<>("Giá cả không được để trống", HttpStatus.BAD_REQUEST);
                        }else{
                            postDTO.setPrice(generalPostDTO.getPrice());
                        }
                    }

                    PostDTO newPostDTO = postService.createPost(postDTO, userId, generalPostDTO.getDirectionId(), generalPostDTO.getPropertyTypeId(),
                            generalPostDTO.getUnitPriceId(), 1, generalPostDTO.getLongevityId());

                    if (generalPostDTO.getImages() != null) {
                        imageService.createImage(generalPostDTO.getImages(), newPostDTO.getPostId());
                    }
                    if (generalPostDTO.getCoordinates() != null) {
                        coordinateService.createCoordinate(generalPostDTO.getCoordinates(), newPostDTO.getPostId());
                    }
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
                    return new ResponseEntity<>("Bạn không thể tạo bài phái sinh từ bài viết của bạn", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Bạn phải chuyển trạng thái sang nhà môi giới", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Bài viết này không cho phép tạo bài phái sinh! ", HttpStatus.CREATED);
        }


        return new ResponseEntity<>("Tạo bài phái sinh thành công!", HttpStatus.CREATED);
    }

    //switch mode of post
    @PutMapping("/allow-derivative/switch/{postId}")
    public ResponseEntity<?> switchPostMode(@RequestHeader(name = "Authorization") String token,
                                            @PathVariable(name = "postId") String id) {
        int postId = Integer.parseInt(id);
        int userId = getUserIdFromToken(token);

        Post post = postRepository.findPostByPostId(postId);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }
        if (post.getOriginalPost() != null) {
            return new ResponseEntity<>("Bài viết là bài phái sinh!", HttpStatus.BAD_REQUEST);
        }
        if (userId == post.getUser().getId()) {
            if (post.isAllowDerivative()) {
                post.setAllowDerivative(false);
                postRepository.save(post);
                return new ResponseEntity<>("Bài viết không cho phép đăng bài phái sinh", HttpStatus.OK);
            } else {
                post.setAllowDerivative(true);
                postRepository.save(post);
                return new ResponseEntity<>("Bài viết cho phép đăng bài phái sinh", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("Người dùng không thể thay đổi chế độ bài viết!", HttpStatus.BAD_REQUEST);
        }
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
        PriceDTO priceDTO = priceService.getPriceByTypeIdAndStatus(1);
        long totalPayment = generalPostDTO.getNumberOfPostedDay() * (priceDTO.getPrice() * (100 - priceDTO.getDiscount()) / 100);
        if (user.getAccountBalance() < totalPayment) {
            return new ResponseEntity<>("Số tiền trong tài khoản không đủ để trả", HttpStatus.BAD_REQUEST);
        } else {
            if (user.getCurrentRole() == 2) {
                long newBalanceAccount = user.getAccountBalance() - totalPayment;
                user.setAccountBalance(newBalanceAccount);
                userRepository.save(user);
                Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");

                if (pattern.matcher(generalPostDTO.getContactName()).find()) {
                    return new ResponseEntity<>("Tên liên lạc không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                }

                if (generalPostDTO.getOwner() != null) {
                    if (pattern.matcher(generalPostDTO.getOwner()).find()) {
                        return new ResponseEntity<>("Tên chủ hộ không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                    }
                }

                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setAmount(totalPayment);
                transactionDTO.setDescription("Thanh toán bài đăng");
                transactionDTO.setTypeId(1);
                transactionDTO.setUser(mapper.map(user, UserDTO.class));
                transactionDTO.setDiscount(priceDTO.getDiscount());
                paymentService.createTransaction(transactionDTO);
                PostDTO postDTO = postService.setDataToPostDTO(generalPostDTO, userId, date, true);
                if(generalPostDTO.getUnitPriceId() ==3 ){
                    postDTO.setPrice(null);
                }else {
                    if(generalPostDTO.getPrice()==null){
                        return new ResponseEntity<>("Giá cả không được để trống", HttpStatus.BAD_REQUEST);
                    }else{
                        postDTO.setPrice(generalPostDTO.getPrice());
                    }
                }
                postDTO.setSpendMoney(totalPayment);
                PostDTO newPostDTO = postService.createPost(postDTO, userId, generalPostDTO.getDirectionId(), generalPostDTO.getPropertyTypeId(),
                        generalPostDTO.getUnitPriceId(), 1, generalPostDTO.getLongevityId());
                if (generalPostDTO.getImages() != null) {
                    imageService.createImage(generalPostDTO.getImages(), newPostDTO.getPostId());
                }
                if (generalPostDTO.getCoordinates() != null) {
                    coordinateService.createCoordinate(generalPostDTO.getCoordinates(), newPostDTO.getPostId());
                }
                switch (generalPostDTO.getPropertyTypeId()) {
                    case 1:
                        ResidentialHouseDTO newResidentialHouse = postService.setDataToResidentialHouse(generalPostDTO);
                        residentialHouseService.createResidentialHouse(newResidentialHouse, newPostDTO.getPostId());
                        return new ResponseEntity<>(" House success", HttpStatus.CREATED);
                    case 2:
                        ApartmentDTO newApartmentDTO = postService.setDataToApartment(generalPostDTO);
                        apartmentService.createApartment(newApartmentDTO, newPostDTO.getPostId());
                        return new ResponseEntity<>("Apartment success", HttpStatus.CREATED);
                    case 3:
                        ResidentialLandDTO newResidentialLand = postService.setDataToResidentialLand(generalPostDTO);
                        residentialLandService.createResidentialLand(newResidentialLand, newPostDTO.getPostId());
                        return new ResponseEntity<>("Land success", HttpStatus.CREATED);
                }
            } else {
                return new ResponseEntity<>("You need change to customer", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
    }

    //get post of user
    @GetMapping("/user")
    public SearchResponse getPostByUserToken(@RequestHeader(name = "Authorization") String token,
                                             @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                             @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                             @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                             @RequestParam(name = "status", defaultValue = "0") String status) {
        int userId = getUserIdFromToken(token);

        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        return postService.getPostByUserId(pageNumber, pageSize, userId, propertyTypeId, sortValue, status);
    }

    @GetMapping("/price-per-day")
    public ResponseEntity<?> getPricePerDay(@RequestHeader(name = "Authorization") String token) {
        PriceDTO priceDTO = priceService.getPriceByTypeIdAndStatus(1);
        return new ResponseEntity<>(priceDTO, HttpStatus.OK);
    }

    @GetMapping("/expired-post/{userId}")
    public ResponseEntity<?> getExpiredPostByUserId(@PathVariable(name = "userId") int userId,
                                                    @RequestHeader(name = "Authorization") String token,
                                                    @RequestParam(name = "pageNo", defaultValue = "0") String pageNo) {
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 2;
        SearchResponse searchResponse = postService.getExpiredPostByUserId(userId, pageNumber, pageSize);

        return new ResponseEntity<>(searchResponse, HttpStatus.OK);
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
    @Transactional
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
            contactService.deleteContactByPostId(postId);
            userCareService.deletePostCareByPostId(postId);
            userFollowPostService.deleteFollowByPostId(postId);
            reportService.deleteReportByPostId(postId);
            postService.deletePost(postId);
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("You are not owner this post", HttpStatus.BAD_REQUEST);
        }

    }

    //search
    @GetMapping
    public ResponseEntity<?> searchPost(@RequestParam(name = "ward", defaultValue = "") String ward,
                                        @RequestParam(name = "district", defaultValue = "") String district,
                                        @RequestParam(name = "province", defaultValue = "") String province,
                                        @RequestParam(name = "minPrice", required = false) String minPrice,
                                        @RequestParam(name = "maxPrice", required = false) String maxPrice,
                                        @RequestParam(name = "minArea", defaultValue = "0") String minArea,
                                        @RequestParam(name = "maxArea", required = false) String maxArea,
                                        @RequestParam(name = "propertyTypes", defaultValue = "1,2,3") List<String> listPropertyType,
                                        @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                        @RequestParam(name = "direction", required = false) List<String> listDirectionId,
                                        @RequestParam(name = "numberOfBedroom", defaultValue = "0") String numberOfBedroom,
                                        @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                        @RequestParam(name = "sortValue", defaultValue = "0") String sortValue
    ) {
        try {
            int numberBedroom = Integer.parseInt(numberOfBedroom);
            int pageSize = 5;
            int pageNumber = Integer.parseInt(pageNo);

            SearchResponse list = postService.searchPosts(ward, district, province, minPrice, maxPrice,
                    minArea, maxArea, listPropertyType, keyword, listDirectionId, numberBedroom,
                    pageNumber, pageSize, sortValue);

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Đã xảy ra lỗi!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/drop-post/{postId}")
    @Transactional
    public ResponseEntity<String> dropPost(@PathVariable int postId,
                                           @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        Post post = postRepository.findPostByPostId(postId);
        if (post != null) {
            if (userId == post.getUser().getId()) {
                postService.changeStatus(postId, 2);
                return new ResponseEntity<>("Cập nhập bài đăng thành công", HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>("Bạn không có quyền ẩn bài đăng này", HttpStatus.BAD_REQUEST);

            }
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }

    }

    //hiển thị lại bài viết
    @PutMapping("/repost/{postId}")
    @Transactional
    public ResponseEntity<String> displayPost(@PathVariable int postId,
                                              @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);

        Post post = postRepository.findPostByPostId(postId);
        if (post != null) {
            if (userId == post.getUser().getId()) {
                postService.changeStatus(postId, 1);
                return new ResponseEntity<>("Cập nhập bài đăng thành công", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Bạn không có quyền sửa bài đăng này", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }

    }


    @PutMapping("/delete-post/{postId}")
    @Transactional
    public ResponseEntity<String> deleteStatusPost(@PathVariable int postId,
                                                   @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        Post post = postRepository.findPostByPostId(postId);
        if (post != null) {
            if (userId == post.getUser().getId()) {
                postService.changeStatus(postId, 6);
                return new ResponseEntity<>("Cập nhập bài đăng thành công", HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>("Bạn không có quyền sửa bài đăng này", HttpStatus.BAD_REQUEST);

            }
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }


    }


    @PutMapping("/extend/{postId}")
    @Transactional
    public ResponseEntity<String> extendPost(@PathVariable int postId,
                                             @Valid @RequestBody NumberOfPostedDayDTO numberOfPostedDayDTO,
                                             @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        PriceDTO priceDTO = priceService.getPriceByTypeIdAndStatus(1);
        Post post = postRepository.findPostByPostId(postId);
        long totalPayment = numberOfPostedDayDTO.getNumberOfPostedDay() * (priceDTO.getPrice() * (100 - priceDTO.getDiscount()) / 100);
        try {
            if (user.getAccountBalance() < totalPayment) {
                return new ResponseEntity<>("Số tiền trong tài khoản không đủ để gia hạn!", HttpStatus.BAD_REQUEST);
            } else {

                if (post.getStatus().getId() == 2 || post.getStatus().getId() == 5) {
                    if (post.getUser().getId() == userId && user.getCurrentRole() == 2) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        if (!post.isBlock() && post.getOriginalPost() == null) {

                            long newBalanceAccount = user.getAccountBalance() - totalPayment;
                            user.setAccountBalance(newBalanceAccount);
                            userRepository.save(user);

                            postService.extendPost(postId, numberOfPostedDayDTO.getNumberOfPostedDay(), totalPayment);
                            TransactionDTO transactionDTO = new TransactionDTO();
                            transactionDTO.setAmount(totalPayment);
                            transactionDTO.setDescription("Gia hạn bài đăng");
                            transactionDTO.setTypeId(1);
                            transactionDTO.setUser(mapper.map(user, UserDTO.class));
                            transactionDTO.setDiscount(priceDTO.getDiscount());
                            paymentService.createTransaction(transactionDTO);
                            return new ResponseEntity<>("Gia hạn bài đăng thành công!", HttpStatus.CREATED);
                        } else {
                            return new ResponseEntity<>("Bài viết bị khóa", HttpStatus.BAD_REQUEST);
                        }

                    } else {
                        return new ResponseEntity<>("Tài khoản phải là chủ bài đăng và khách hàng", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>("Bài viết không thể gia hạn", HttpStatus.OK);

                }


            }


        } catch (Exception e) {

        }
        return new ResponseEntity<>("Không gia hạn thành công", HttpStatus.BAD_REQUEST);

    }

    @PutMapping("/{postId}")
    @Transactional
    public ResponseEntity<String> updatePost(@PathVariable int postId,
                                             @Valid @RequestBody GeneralPostDTO generalPostDTO,
                                             @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);


        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        int propertyId = post.getPropertyType().getId();
        if (userId == post.getUser().getId()) {
            Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
            if (pattern.matcher(generalPostDTO.getContactName()).find()) {
                return new ResponseEntity<>("Tên liên lạc không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
            }
            if (generalPostDTO.getOwner() != null) {
                if (pattern.matcher(generalPostDTO.getOwner()).find()) {
                    return new ResponseEntity<>("Tên chủ hộ không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                }
            }
            if(generalPostDTO.getUnitPriceId() == 3 ){
                generalPostDTO.setPrice(null);
            }else {
                if(generalPostDTO.getPrice()==null){
                    return new ResponseEntity<>("Giá cả không được để trống", HttpStatus.BAD_REQUEST);
                }
            }
            PostDTO newPostDTO = postService.updatePost(generalPostDTO, post, userId, generalPostDTO.getImages());
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
        } else {
            return new ResponseEntity<>("Bạn Không phải là chủ bài đăng", HttpStatus.BAD_REQUEST);
        }

    }

    //user follow post
    @PostMapping("/follow/{postId}")
    public ResponseEntity<?> createUserFollowPost(@PathVariable(name = "postId", required = false) String postId,
                                                  @RequestHeader(name = "Authorization") String token) {

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
                                                 @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                                 @RequestParam(name = "sortValue", defaultValue = "0") String sortValue) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        SearchResponse list = userFollowPostService.getFollowPostByUserPaging(phone, propertyTypeId, pageNumber, pageSize, sortValue);

//        List<DerivativeDTO> listAll = userFollowPostService.getFollowPostByUser(phone, propertyTypeId);
//        int totalPage = 0;
//        if (listAll.size() % pageSize == 0) {
//            totalPage = listAll.size() / pageSize;
//        } else {
//            totalPage = (listAll.size() / pageSize) + 1;
//        }
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("lists", list);
//        map.put("totalResult", listAll.size());
//        map.put("pageNo", pageNumber + 1);
//        map.put("totalPages", totalPage);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/user/follow/short")
    public ResponseEntity<?> getTitleFollowPostOfUser(@RequestHeader(name = "Authorization") String token) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");

        List<ShortPostDTO> list = userFollowPostService.getShortFollowPostByUser(phone);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/history/{postId}")
    public ResponseEntity<?> getRealEstateHistory(@PathVariable(name = "postId") int postId) {

        Map<String, List> history = postService.getRealEstateHistory(postId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    //list all post and derivative_posts
    @GetMapping("/lists")
    public ResponseEntity<?> getListPostAndDerivativePost(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                          @RequestParam(name = "sortValue", defaultValue = "0") String sortValue) {
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        List<DerivativeDTO> lists = postService.getAllDerivativePostPaging(pageNumber, pageSize, sortValue);

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

    //get list all post of broker
    @GetMapping("/broker/list")
    public ResponseEntity<?> getListDerivativePostByUserId(@RequestHeader(name = "Authorization") String token,
                                                           @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                           @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                                           @RequestParam(name = "sortValue", defaultValue = "0") String sortValue
    ) {
        try {
            int userId = getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            if (user.getCurrentRole() != 3) {
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
        } catch (Exception e) {
            return new ResponseEntity<>("Trang này không tồn tại!", HttpStatus.BAD_REQUEST);
        }
    }

    //get list derivative post of broker
    @GetMapping("/derivative/list")
    public ResponseEntity<?> getListDerivativePostOfBroker(@RequestHeader(name = "Authorization") String token,
                                                           @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                           @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                                           @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                                           @RequestParam(name = "status", defaultValue = "0") String status
    ) {
        try {
            int userId = getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            if (user.getCurrentRole() != 3) {
                return new ResponseEntity<>("Người dùng không phải là broker!", HttpStatus.OK);
            }
            int pageNumber = Integer.parseInt(pageNo);
            int pageSize = 8;

            SearchResponse lists = postService.getDerivativePostOfBrokerPaging(userId, propertyTypeId, pageNumber, pageSize, sortValue, status);

            return new ResponseEntity<>(lists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Trang này không tồn tại!", HttpStatus.BAD_REQUEST);
        }
    }

    //get list post by user id
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getListPostByUserId(@PathVariable(name = "userId") String id,
                                                 @RequestParam(name = "propertyType", required = false) String propertyTypeId,
                                                 @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                 @RequestParam(name = "sortValue", defaultValue = "0") String sortValue) {
        int userId = Integer.parseInt(id);
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 6;

        SearchResponse searchResponse = postService.getAllPostByUserId(pageNumber, pageSize, userId, propertyTypeId, sortValue);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

//        userDTO.setBroker(true);
        Boolean isBroker = false;
        Set<Role> setRole = user.getRoles();
        Role role = roleRepository.findByName("BROKER").get();
        if (setRole.contains(role)) {
            isBroker = true;
        }
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setBroker(isBroker);
        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(user.getId(), 3);
        if (avgRate != null) {
            userDTO.setAvgRate(avgRate.getAvgRate());
        } else {
            userDTO.setAvgRate(0);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("lists", searchResponse);
        map.put("user", userDTO);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //get list original post for broker choose
    @GetMapping("/broker/original")
    public ResponseEntity<?> getListPostForBroker(@RequestHeader(name = "Authorization") String token,
                                                  @RequestParam(name = "ward", defaultValue = "") String ward,
                                                  @RequestParam(name = "district", defaultValue = "") String district,
                                                  @RequestParam(name = "province", defaultValue = "") String province,
                                                  @RequestParam(name = "minPrice", required = false) String minPrice,
                                                  @RequestParam(name = "maxPrice", required = false) String maxPrice,
                                                  @RequestParam(name = "minArea", defaultValue = "0") String minArea,
                                                  @RequestParam(name = "maxArea", required = false) String maxArea,
                                                  @RequestParam(name = "propertyType", defaultValue = "1,2,3") List<String> listPropertyType,
                                                  @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                  @RequestParam(name = "direction", required = false) List<String> listDirectionId,
                                                  @RequestParam(name = "numberOfBedroom", defaultValue = "0") String numberOfBedroom,
                                                  @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                  @RequestParam(name = "sortValue", defaultValue = "0") String sortValue
    ) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getCurrentRole() == 3) {
//            int pageNumber = Integer.parseInt(pageNo);
//            int pageSize = 5;
//            SearchResponse searchResponse = postService.getAllPostForBroker(pageNumber, pageSize, sortValue);
//            return new ResponseEntity<>(searchResponse, HttpStatus.OK);
            try {
                int numberBedroom = Integer.parseInt(numberOfBedroom);
                int pageSize = 5;
                int pageNumber = Integer.parseInt(pageNo);

                SearchResponse list = postService.searchOriginalPosts(ward, district, province, minPrice, maxPrice,
                        minArea, maxArea, listPropertyType, keyword, listDirectionId, numberBedroom,
                        pageNumber, pageSize, sortValue, userId);

                return new ResponseEntity<>(list, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Đã xảy ra lỗi!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Người dùng không phải là broker !", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/categories")
    public ResponseEntity<?> getNumberOfPropertyType() {
        Map<String, Integer> map = postService.getNumberOfPropertyType();
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/broker/categories")
    public ResponseEntity<?> getNumberOfPropertyTypeForBroker(@RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        Map<String, Integer> map = postService.getNumberOfPropertyTypeForBroker(userId);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Autowired
    SimpMessagingTemplate template;

    @PostMapping("/history/{postId}")
//    @Transactional
    public ResponseEntity<?> createRealEstateHistory(@PathVariable(name = "postId") int postId,
                                                     @RequestBody HistoryDTO historyDTO) {
        Post post = postRepository.findPostByPostId(postId);
        if (post != null && post.getStatus().getId() == 1 && post.getOriginalPost() == null) {
            Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
            if (pattern.matcher(historyDTO.getOwner()).find()) {
                return new ResponseEntity<>("Họ và tên không chứa ký tự đặc biệt và số!", HttpStatus.BAD_REQUEST);
            }
            long spendMoney = post.getSpendMoney();
            long refundMoney = 0;
            RefundPercentDTO refundPercentDTO = new RefundPercentDTO();
            refundPercentDTO = refundPercentService.getActiveRefundPercent(1);
            refundMoney = spendMoney * (100 - refundPercentDTO.getPercent()) / 100;
            if (historyDTO.isProvideInfo()) {
                switch (historyDTO.getTypeId()) {
                    case 1:
                        ResidentialHouseHistoryDTO houseHistoryDTO = new ResidentialHouseHistoryDTO();
                        residentialHouseHistoryService.setDataToResidentialHouseHistoryDTO(houseHistoryDTO, historyDTO);
                        ResidentialHouseHistoryDTO houseDto = residentialHouseHistoryService.createResidentialHouseHistory(houseHistoryDTO);
                        if (houseDto == null) {
                            return new ResponseEntity<>("Mã vạch đã bị trùng!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case 2:
                        ApartmentHistoryDTO apartmentHistoryDTO = new ApartmentHistoryDTO();
                        apartmentHistoryService.setDataToApartmentHistoryDTO(apartmentHistoryDTO, historyDTO);
                        ApartmentHistoryDTO dto = apartmentHistoryService.createApartmentHistory(apartmentHistoryDTO);
                        if (dto == null) {
                            return new ResponseEntity<>("Mã vạch đã bị trùng!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case 3:
                        ResidentialLandHistoryDTO landHistoryDTO = new ResidentialLandHistoryDTO();
                        residentialLandHistoryService.setDataToResidentialLandHistoryDTO(landHistoryDTO, historyDTO);
                        ResidentialLandHistoryDTO landDto = residentialLandHistoryService.createResidentialLandHistory(landHistoryDTO);
                        if (landDto == null) {
                            return new ResponseEntity<>("Mã vạch đã bị trùng!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                }
                refundPercentDTO = refundPercentService.getActiveRefundPercent(2);
                refundMoney = spendMoney * (100 - refundPercentDTO.getPercent()) / 100;
            }
            post.setStatus(new Status(3));
            post.setSpendMoney(spendMoney - refundMoney);
            postRepository.save(post);
            postService.changeStatusOfDerivativePostOfPost(postId);

            //tinh tien hoan tra cho nguoi dung dang bai


            int userId = post.getUser().getId();
            TextMessageDTO messageDTO = new TextMessageDTO();
            String message = "Bạn được hoàn lại " + refundMoney + " VNĐ";
            messageDTO.setMessage(message);
            template.convertAndSend("/topic/message/" + userId, messageDTO);

            //save notification table
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserId(userId);
            notificationDTO.setContent(message);
//            notificationDTO.setPhone(userRequest.getPhone());
            notificationDTO.setType("Refund");
            notificationService.createContactNotification(notificationDTO);

            //update unread notification user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            int numberUnread = user.getUnreadNotification();
            numberUnread++;
            user.setUnreadNotification(numberUnread);
            long accountBalance = user.getAccountBalance();
            user.setAccountBalance(accountBalance + refundMoney);
            userRepository.save(user);

            List<Post> listPostDto = postRepository.getDerivativePostOfOriginalPost(postId);
            for(Post p: listPostDto){
                User u = p.getUser();
                TextMessageDTO messageDTO1 = new TextMessageDTO();
                String message1 = "Giao dịch đã kết thúc. Vui lòng đánh giá người đăng bài!";
                messageDTO.setMessage(message1);
                template.convertAndSend("/topic/message/" + u.getId(), messageDTO1);

                NotificationDTO notification = new NotificationDTO();
                notification.setUserId(p.getUser().getId());
                notification.setContent(message);
                notification.setSender(userId);
                notification.setType("FinishTransaction");
                notificationService.createContactNotification(notificationDTO);

                int unread = u.getUnreadNotification();
                unread++;
                u.setUnreadNotification(unread);
                userRepository.save(u);
            }

            return new ResponseEntity<>("Hoàn thành giao dịch!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại hoặc không hoạt động!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/original/{userId}")
    public ResponseEntity<?> getAllOriginalPostOfUser(@PathVariable(name = "userId") int userId,
                                                      @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                      @RequestParam(name = "sortValue", defaultValue = "0") String sortValue) {
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 6;

        SearchResponse searchResponse = postService.getAllOriginalPostByUserId(userId, pageNumber, pageSize, sortValue);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

//        userDTO.setBroker(true);
        Boolean isBroker = false;
        Set<Role> setRole = user.getRoles();
        Role role = roleRepository.findByName("BROKER").get();
        if (setRole.contains(role)) {
            isBroker = true;
        }
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setBroker(isBroker);
        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(user.getId(), user.getCurrentRole());
        if (avgRate != null) {
            userDTO.setAvgRate(avgRate.getAvgRate());
        } else {
            userDTO.setAvgRate(0);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("lists", searchResponse);
        map.put("user", userDTO);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/outstanding")
    public ResponseEntity<?> getOutstandingPost(){
        List<SearchDTO> list = postService.getOutstandingPost();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
