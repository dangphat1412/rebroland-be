package vn.edu.fpt.rebroland.controller;


import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.*;
import vn.edu.fpt.rebroland.service.*;
import com.pusher.rest.Pusher;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
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
        if ((postDTO != null) && (!postDTO.isBlock())) {
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
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/my-property/{postId}")
    public ResponseEntity<?> checkDetailPostOfUser(@PathVariable(name = "postId") int postId,
                                                   @RequestHeader(name = "Authorization") String token) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        int userId = getUserIdFromToken(token);
        PostDTO postDTO = postService.getAllPostByPostId(postId);
        if (postDTO != null) {
            if (postDTO.getUser().getId() == userId) {
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
            } else {
                return new ResponseEntity<>("Bài viết không phải của người dùng!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/original-detail/{postId}")
    public ResponseEntity<?> getDetailOriginalPost(@PathVariable(name = "postId") int postId,
                                                   @RequestHeader(name = "Authorization") String token) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        int userId = getUserIdFromToken(token);
        PostDTO postDTO = postService.getActiveOrFinishPostById(postId);
        if ((postDTO != null) && (!postDTO.isBlock())) {
            if (postDTO.getUser().getId() == userId) {
                return new ResponseEntity<>("Bài viết này đã thuộc về khách hàng!", HttpStatus.BAD_REQUEST);
            }
            if (!postDTO.isAllowDerivative()) {
                return new ResponseEntity<>("Bài viết không cho phép tạo bài phái sinh!", HttpStatus.BAD_REQUEST);
            }
            if (postDTO.getOriginalPost() == null) {
                PostDTO dto = postService.getDerivativePostOfUser(userId, postId);
                if (dto == null) {
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
                    map.put("isAllowDerivative", true);
                    return new ResponseEntity<>(map, HttpStatus.OK);
                } else {
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
                    map.put("isAllowDerivative", false);
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("Bài viết không phải là bài gốc!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại hoặc là bị chặn!", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/detail/{postId}")
    public ResponseEntity<?> getPost(@PathVariable(name = "postId") int postId) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        PostDTO postDTO = postService.getActiveOrFinishPostById(postId);
        if ((postDTO != null) && (!postDTO.isBlock())) {
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
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại hoặc là bị chặn!", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/all/detail/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable(name = "postId") int postId) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        PostDTO postDTO = postService.findPostByPostId(postId);
        if (postDTO != null) {
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
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại hoặc là bị chặn!", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/my-derivative/{postId}")
    public ResponseEntity<?> getDetailDerivativePost(@PathVariable(name = "postId") int postId,
                                                     @RequestHeader(name = "Authorization") String token) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        int userId = getUserIdFromToken(token);
        PostDTO postDTO = postService.getDerivativePostByPostId(postId);
//        if ((postDTO != null) && (!postDTO.isBlock())) {
        if ((postDTO != null)) {
            if (postDTO.getOriginalPost() != null) {
                if (postDTO.getUser().getId() == userId) {
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
                } else {
                    return new ResponseEntity<>("Bài viết không phải của người dùng!", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Bài viết không phải là bài phái sinh!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }

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
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, 7);
        java.sql.Date sqlDate = new java.sql.Date(c.getTimeInMillis());

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Post post = postRepository.findPostByPostId(postId);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại", HttpStatus.BAD_REQUEST);
        }
        Post checkPost = postRepository.getPostByUserIdAndOriginalId(postId, userId);
        if (checkPost != null) {
            return new ResponseEntity<>("Bạn đã tạo bài phái sinh cho bài viết này", HttpStatus.BAD_REQUEST);
        }


        if (post.isAllowDerivative() && post.getOriginalPost() == null) {
            if (user.getCurrentRole() == 3) {
                if (user.getId() != post.getUser().getId()) {
                    Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
                    if (pattern.matcher(generalPostDTO.getContactName().trim()).find()) {
                        return new ResponseEntity<>("Tên liên lạc không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                    }

                    if (generalPostDTO.isCertification()) {
                        if (generalPostDTO.getPropertyTypeId() == 2) {
                            if ((generalPostDTO.getBarcode() == null || generalPostDTO.getBarcode().equals("")) &&
                                    (generalPostDTO.getPlotNumber() == null) &&
                                    (generalPostDTO.getBuildingName() == null || generalPostDTO.getBuildingName().equals("")) &&
                                    (generalPostDTO.getRoomNumber() == null || generalPostDTO.getRoomNumber().equals(""))) {
                                generalPostDTO.setBarcode(null);
                                generalPostDTO.setPlotNumber(null);
                                generalPostDTO.setBuildingName(null);
                                generalPostDTO.setRoomNumber(null);
                            } else if ((generalPostDTO.getBarcode() != null || !generalPostDTO.getBarcode().equals("")) &&
                                    (generalPostDTO.getPlotNumber() != null) &&
                                    (generalPostDTO.getBuildingName() != null || !generalPostDTO.getBuildingName().equals("")) &&
                                    (generalPostDTO.getRoomNumber() != null || !generalPostDTO.getRoomNumber().equals(""))) {
                                generalPostDTO.setBarcode(generalPostDTO.getBarcode());
                                generalPostDTO.setPlotNumber(generalPostDTO.getPlotNumber());
                                generalPostDTO.setBuildingName(generalPostDTO.getBuildingName());
                                generalPostDTO.setRoomNumber(generalPostDTO.getRoomNumber());
                            } else {
                                return new ResponseEntity<>("Bạn phải điền đủ 4 trường ở trên.", HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            if ((generalPostDTO.getBarcode() == null || generalPostDTO.getBarcode().equals("")) &&
                                    (generalPostDTO.getPlotNumber() == null)) {
                                generalPostDTO.setBarcode(null);
                                generalPostDTO.setPlotNumber(null);
                            } else if ((generalPostDTO.getBarcode() != null || !generalPostDTO.getBarcode().equals("")) &&
                                    (generalPostDTO.getPlotNumber() != null)) {
                                generalPostDTO.setBarcode(generalPostDTO.getBarcode());
                                generalPostDTO.setPlotNumber(generalPostDTO.getPlotNumber());
                            } else {
                                return new ResponseEntity<>("Bạn phải điền đủ 2 trường ở trên.", HttpStatus.BAD_REQUEST);
                            }
                        }

                    }
                    if (generalPostDTO.getOwner() != null) {
                        if (pattern.matcher(generalPostDTO.getOwner().trim()).find()) {
                            return new ResponseEntity<>("Tên chủ hộ không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                        }
                    }
                    PostDTO postDTO = postService.setDataToPostDTO(generalPostDTO, userId, sqlDate, false);
                    postDTO.setOriginalPost(postId);
                    postDTO.setTransactionStartDate(post.getTransactionStartDate());
                    postDTO.setTransactionEndDate(post.getTransactionEndDate());
                    if (generalPostDTO.getUnitPriceId() == 3) {
                        postDTO.setPrice(null);
                    } else {
                        if (generalPostDTO.getPrice() == null) {
                            return new ResponseEntity<>("Giá cả không được để trống", HttpStatus.BAD_REQUEST);
                        } else {
                            postDTO.setPrice(generalPostDTO.getPrice());
                        }
                    }

                    PostDTO newPostDTO = postService.createPost(postDTO, userId, generalPostDTO.getDirectionId(), generalPostDTO.getPropertyTypeId(),
                            generalPostDTO.getUnitPriceId(), 1, generalPostDTO.getLongevityId());

                    if ((generalPostDTO.getImages() != null) && (generalPostDTO.getImages().size() != 0)) {
                        imageService.createImage(generalPostDTO.getImages(), newPostDTO.getPostId());
                    }
                    if ((generalPostDTO.getCoordinates() != null) && (generalPostDTO.getCoordinates().size() != 0)) {
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

        String message = "SĐT " + user.getPhone() + " đã tạo bài phái sinh cho bài viết mã số " + postId + " của bạn!";
        Pusher push = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
        push.setCluster("ap1");
        push.setEncrypted(true);
        push.trigger("my-channel-" + post.getUser().getId(), "my-event", Collections.singletonMap("message", message));

        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(post.getUser().getId());
        notification.setContent(message);
        notification.setSender(userId);
        notification.setPostId(postId);
        notification.setType("CreateDerivative");
        notificationService.createContactNotification(notification);

        int unread = post.getUser().getUnreadNotification();
        unread++;
        post.getUser().setUnreadNotification(unread);
        userRepository.save(post.getUser());

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
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, 7);
        java.sql.Date sqlDate = new java.sql.Date(c.getTimeInMillis());

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

                if (pattern.matcher(generalPostDTO.getContactName().trim()).find()) {
                    return new ResponseEntity<>("Tên liên lạc không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                }


                if (generalPostDTO.isCertification()) {
                    if (generalPostDTO.getPropertyTypeId() == 2) {
                        if ((generalPostDTO.getBarcode() == null || generalPostDTO.getBarcode().equals("")) &&
                                (generalPostDTO.getPlotNumber() == null) &&
                                (generalPostDTO.getOwner() == null || generalPostDTO.getOwner().equals("")) &&
                                (generalPostDTO.getOwnerPhone() == null || generalPostDTO.getOwnerPhone().equals("")) &&
                                (generalPostDTO.getBuildingName() == null || generalPostDTO.getBuildingName().equals("")) &&
                                (generalPostDTO.getRoomNumber() == null || generalPostDTO.getRoomNumber().equals(""))) {
                            generalPostDTO.setBarcode(null);
                            generalPostDTO.setPlotNumber(null);
                            generalPostDTO.setOwner(null);
                            generalPostDTO.setOwnerPhone(null);
                            generalPostDTO.setBuildingName(null);
                            generalPostDTO.setRoomNumber(null);
                        } else if ((generalPostDTO.getBarcode() != null || !generalPostDTO.getBarcode().equals("")) &&
                                (generalPostDTO.getPlotNumber() != null) &&
                                (generalPostDTO.getOwner() != null || !generalPostDTO.getOwner().equals("")) &&
                                (generalPostDTO.getOwnerPhone() != null || !generalPostDTO.getOwnerPhone().equals("")) &&
                                (generalPostDTO.getBuildingName() != null || !generalPostDTO.getBuildingName().equals("")) &&
                                (generalPostDTO.getRoomNumber() != null || !generalPostDTO.getRoomNumber().equals(""))) {
                            generalPostDTO.setBarcode(generalPostDTO.getBarcode());
                            generalPostDTO.setPlotNumber(generalPostDTO.getPlotNumber());
                            generalPostDTO.setOwner(generalPostDTO.getOwner());
                            generalPostDTO.setOwnerPhone(generalPostDTO.getOwnerPhone());
                            generalPostDTO.setBuildingName(generalPostDTO.getBuildingName());
                            generalPostDTO.setRoomNumber(generalPostDTO.getRoomNumber());
                        } else {
                            return new ResponseEntity<>("Bạn phải điền đủ 6 trường ở trên.", HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        if ((generalPostDTO.getBarcode() == null || generalPostDTO.getBarcode().equals("")) &&
                                (generalPostDTO.getPlotNumber() == null) &&
                                (generalPostDTO.getOwner() == null || generalPostDTO.getOwner().equals("")) &&
                                (generalPostDTO.getOwnerPhone() == null || generalPostDTO.getOwnerPhone().equals(""))) {
                            generalPostDTO.setBarcode(null);
                            generalPostDTO.setPlotNumber(null);
                            generalPostDTO.setOwner(null);
                            generalPostDTO.setOwnerPhone(null);
                        } else if ((generalPostDTO.getBarcode() != null || !generalPostDTO.getBarcode().equals("")) &&
                                (generalPostDTO.getPlotNumber() != null) &&
                                (generalPostDTO.getOwner() != null || !generalPostDTO.getOwner().equals("")) &&
                                (generalPostDTO.getOwnerPhone() != null || !generalPostDTO.getOwnerPhone().equals(""))) {
                            generalPostDTO.setBarcode(generalPostDTO.getBarcode());
                            generalPostDTO.setPlotNumber(generalPostDTO.getPlotNumber());
                            generalPostDTO.setOwner(generalPostDTO.getOwner());
                            generalPostDTO.setOwnerPhone(generalPostDTO.getOwnerPhone());
                        } else {
                            return new ResponseEntity<>("Bạn phải điền đủ 4 trường ở trên.", HttpStatus.BAD_REQUEST);
                        }
                    }

                }
                if (generalPostDTO.getOwner() != null) {
                    if (pattern.matcher(generalPostDTO.getOwner().trim()).find()) {
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
                PostDTO postDTO = postService.setDataToPostDTO(generalPostDTO, userId, sqlDate, true);
                if (generalPostDTO.getUnitPriceId() == 3) {
                    postDTO.setPrice(null);
                } else {
                    if (generalPostDTO.getPrice() == null) {
                        return new ResponseEntity<>("Giá cả không được để trống", HttpStatus.BAD_REQUEST);
                    } else {
                        postDTO.setPrice(generalPostDTO.getPrice());
                    }
                }
                postDTO.setSpendMoney(totalPayment);
                PostDTO newPostDTO = postService.createPost(postDTO, userId, generalPostDTO.getDirectionId(), generalPostDTO.getPropertyTypeId(),
                        generalPostDTO.getUnitPriceId(), 1, generalPostDTO.getLongevityId());
                if ((generalPostDTO.getImages() != null) && (generalPostDTO.getImages().size() != 0)) {
                    imageService.createImage(generalPostDTO.getImages(), newPostDTO.getPostId());
                }
                if ((generalPostDTO.getCoordinates() != null) && (generalPostDTO.getCoordinates().size() != 0)) {
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
                    result = "Xóa nhà thành công";
                    break;
                case 2:
                    apartmentService.deleteApartmentByPostId(postId);
                    result = "Xóa chung cư thành công";
                    break;
                case 3:
                    residentialLandService.deleteResidentialLandByPostId(postId);
                    result = "Xóa đất thành công";
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
            return new ResponseEntity<>("Bạn Không phải là chủ bài đăng này", HttpStatus.BAD_REQUEST);
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

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        if (post != null && post.getStatus().getId() == 2) {
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
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
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
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại", HttpStatus.BAD_REQUEST);
        }
        long totalPayment = numberOfPostedDayDTO.getNumberOfPostedDay() * (priceDTO.getPrice() * (100 - priceDTO.getDiscount()) / 100);

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


    }

    @PutMapping("/{postId}")
    @Transactional
    public ResponseEntity<String> updatePost(@PathVariable int postId,
                                             @Valid @RequestBody GeneralPostDTO generalPostDTO,
                                             @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);


        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại", HttpStatus.BAD_REQUEST);
        }
        int propertyId = post.getPropertyType().getId();
        if (userId == post.getUser().getId()) {
            Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
            if (pattern.matcher(generalPostDTO.getContactName().trim()).find()) {
                return new ResponseEntity<>("Tên liên lạc không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
            }
            if (generalPostDTO.isCertification()) {
                if (generalPostDTO.getPropertyTypeId() == 2) {
                    if ((generalPostDTO.getBarcode() == null || generalPostDTO.getBarcode().equals("")) &&
                            (generalPostDTO.getPlotNumber() == null) &&
                            (generalPostDTO.getBuildingName() == null || generalPostDTO.getBuildingName().equals("")) &&
                            (generalPostDTO.getRoomNumber() == null || generalPostDTO.getRoomNumber().equals(""))) {
                        generalPostDTO.setBarcode(null);
                        generalPostDTO.setPlotNumber(null);
                        generalPostDTO.setBuildingName(null);
                        generalPostDTO.setRoomNumber(null);
                    } else if ((generalPostDTO.getBarcode() != null || !generalPostDTO.getBarcode().equals("")) &&
                            (generalPostDTO.getPlotNumber() != null) &&
                            (generalPostDTO.getBuildingName() != null || !generalPostDTO.getBuildingName().equals("")) &&
                            (generalPostDTO.getRoomNumber() != null || !generalPostDTO.getRoomNumber().equals(""))) {
                        generalPostDTO.setBarcode(generalPostDTO.getBarcode());
                        generalPostDTO.setPlotNumber(generalPostDTO.getPlotNumber());
                        generalPostDTO.setBuildingName(generalPostDTO.getBuildingName());
                        generalPostDTO.setRoomNumber(generalPostDTO.getRoomNumber());
                    } else {
                        return new ResponseEntity<>("Bạn phải điền đủ 4 trường ở trên.", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    if ((generalPostDTO.getBarcode() == null || generalPostDTO.getBarcode().equals("")) &&
                            (generalPostDTO.getPlotNumber() == null)) {
                        generalPostDTO.setBarcode(null);
                        generalPostDTO.setPlotNumber(null);
                    } else if ((generalPostDTO.getBarcode() != null || !generalPostDTO.getBarcode().equals("")) &&
                            (generalPostDTO.getPlotNumber() != null)) {
                        generalPostDTO.setBarcode(generalPostDTO.getBarcode());
                        generalPostDTO.setPlotNumber(generalPostDTO.getPlotNumber());
                    } else {
                        return new ResponseEntity<>("Bạn phải điền đủ 2 trường ở trên.", HttpStatus.BAD_REQUEST);
                    }
                }

            }
            if (generalPostDTO.getOwner() != null) {
                if (pattern.matcher(generalPostDTO.getOwner().trim()).find()) {
                    return new ResponseEntity<>("Tên chủ hộ không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
                }
            }
            if (generalPostDTO.getUnitPriceId() == 3) {
                generalPostDTO.setPrice(null);
            } else {
                if (generalPostDTO.getPrice() == null) {
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
            if ((generalPostDTO.getImages() != null) && (generalPostDTO.getImages().size() != 0)) {
                imageService.updateImage(generalPostDTO.getImages(), postId);
            } else {
                imageService.deleteImageByPostId(postId);
            }
            if ((generalPostDTO.getCoordinates() != null) && (generalPostDTO.getCoordinates().size() != 0)) {
                coordinateService.updateCoordinate(generalPostDTO.getCoordinates(), postId);

            } else {
                coordinateService.deleteCoordinateByPostId(postId);
            }

            return new ResponseEntity<>("Cập nhập thành công bài viết", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bạn không phải là chủ bài đăng", HttpStatus.BAD_REQUEST);
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
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }
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
            int pageSize = 5;

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
        User user = userRepository.getUserById(userId);

        if ((user != null)) {
            if (user.isBlock() == true) {
                return new ResponseEntity<>("Người dùng đã bị chặn!", HttpStatus.BAD_REQUEST);
            }
            //        userDTO.setBroker(true);
            Boolean isBroker = false;
            Set<Role> setRole = user.getRoles();
            Role role = roleRepository.findByName("BROKER").get();
            if (setRole.contains(role)) {
//                isBroker = true;
                UserDTO userDTO = mapper.map(user, UserDTO.class);
//            userDTO.setBroker(isBroker);
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
            } else {
                return new ResponseEntity<>("Người dùng không phải broker!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Người dùng không tồn tại!", HttpStatus.BAD_REQUEST);
        }
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
    private OtpService otpService;

    @PostMapping("/history/send-otp/{postId}")
    public ResponseEntity<?> sendOtpToOwnerPhone(@PathVariable(name = "postId") int postId,
                                                 @Valid @RequestBody HistoryDTO historyDTO) {
        Post post = postRepository.getActiveOrFinishOrExpirePostById(postId);
        if (post != null && post.getOriginalPost() == null) {
            if (historyDTO.getOwner() != null) {
                Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
                if (pattern.matcher(historyDTO.getOwner()).find()) {
                    return new ResponseEntity<>("Họ và tên không chứa ký tự đặc biệt và số!", HttpStatus.BAD_REQUEST);
                }
            }
            if (historyDTO.isProvideInfo()) {
                String startDate = "";
                if (historyDTO.getBarcode().length() == 13) {
                    startDate = "20" + historyDTO.getBarcode().substring(5, 7);
                } else {
                    startDate = "20" + historyDTO.getBarcode().substring(7, 9);
                }
                try {
                    int year = Integer.parseInt(startDate);
                    Calendar instance = Calendar.getInstance();
                    int yearNow = instance.get(Calendar.YEAR);
                    if ((year > yearNow) || (year < 2009)) {
                        return new ResponseEntity<>("Mã vạch sai!", HttpStatus.BAD_REQUEST);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>("Mã vạch không hợp lệ!", HttpStatus.BAD_REQUEST);
                }
                switch (historyDTO.getTypeId()) {
                    case 1:
                        ResidentialHouseHistoryDTO houseDto = residentialHouseHistoryService.getResidentialHouseHistoryByBarcode(historyDTO.getBarcode());
                        if (houseDto != null) {
                            return new ResponseEntity<>("Thông tin bất động sản đã tồn tại!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case 2:
                        if (historyDTO.getRoomNumber() == null || historyDTO.getRoomNumber().isEmpty()) {
                            return new ResponseEntity<>("Số phòng không được để trống!", HttpStatus.BAD_REQUEST);
                        }
                        if (historyDTO.getBuildingName() == null || historyDTO.getBuildingName().isEmpty()) {
                            return new ResponseEntity<>("Tên tòa nhà không được để trống!", HttpStatus.BAD_REQUEST);
                        }
                        ApartmentHistoryDTO dto = apartmentHistoryService.getApartmentHistoryByBarcode(historyDTO.getBarcode());
                        if (dto != null) {
                            return new ResponseEntity<>("Thông tin bất động sản đã tồn tại!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case 3:
                        ResidentialLandHistoryDTO landDto = residentialLandHistoryService.getResidentialLandHistoryByBarcode(historyDTO.getBarcode());
                        if (landDto != null) {
                            return new ResponseEntity<>("Thông tin bất động sản đã tồn tại!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                }
            }
            String token = otpService.generateOtp(historyDTO.getPhone()) + "";
            otpService.remainCount(historyDTO.getPhone(), 3);
            sendSMS(historyDTO.getPhone(), "Mã OTP của bạn là: " + token);
            Map<String, Object> map = new HashMap<>();
            map.put("historyData", historyDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES);
            map.put("remainTime", 3);
            map.put("otp", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại hoặc không hoạt động!", HttpStatus.BAD_REQUEST);
        }
    }

    public void sendSMS(String phone, String token) {
        Twilio.init("ACd79616329e4784b2208b5f134088905d",
                "dc59c60fcf2b169f0b4fc37fbb8da680");

        Message.creator(new PhoneNumber(phone.replaceFirst("0", "+84")),
                new PhoneNumber("+19844647230"), token).create();
    }

    @PostMapping("/history/{postId}")
//    @Transactional
    public ResponseEntity<?> createRealEstateHistory(@PathVariable(name = "postId") int postId,
                                                     @Valid @RequestBody HistoryDTO historyDTO) {
        Post post = postRepository.getActiveOrFinishOrExpirePostById(postId);
        if (post.getOriginalPost() != null) {
            return new ResponseEntity<>("Không được kết thúc giao dịch ở bài phái sinh!", HttpStatus.BAD_REQUEST);
        }
        if (post != null && post.getStatus().getId() == 1) {
            long spendMoney = post.getSpendMoney();
            long refundMoney = 0;
            RefundPercentDTO refundPercentDTO = new RefundPercentDTO();
            refundPercentDTO = refundPercentService.getActiveRefundPercent(1);
            refundMoney = spendMoney * (refundPercentDTO.getPercent()) / 100;
            if (historyDTO.isProvideInfo()) {
                int remainTime = 3;
                if (otpService.getCount(historyDTO.getPhone()) != null) {
                    remainTime = otpService.getCount(historyDTO.getPhone());
                }
                if (remainTime == 0) {
                    otpService.clearCount(historyDTO.getPhone());
                    otpService.clearOtp(historyDTO.getPhone());
                    return new ResponseEntity<>("Nhập sai quá số lần quy định!", HttpStatus.BAD_REQUEST);
                }
                if (historyDTO.getOwner() != null) {
                    Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
                    if (pattern.matcher(historyDTO.getOwner()).find()) {
                        return new ResponseEntity<>("Họ và tên không chứa ký tự đặc biệt và số!", HttpStatus.BAD_REQUEST);
                    }
                }
                if (otpService.getOtp(historyDTO.getPhone()) == null) {
                    return new ResponseEntity<>("Mã OTP đã hết hạn!", HttpStatus.BAD_REQUEST);
                }
                int otp = Integer.parseInt(historyDTO.getToken());
                if (otp != otpService.getOtp(historyDTO.getPhone())) {
                    otpService.remainCount(historyDTO.getPhone(), remainTime);
                    return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
                }
                switch (historyDTO.getTypeId()) {
                    case 1:
                        ResidentialHouseHistoryDTO houseHistoryDTO = new ResidentialHouseHistoryDTO();
                        residentialHouseHistoryService.setDataToResidentialHouseHistoryDTO(houseHistoryDTO, historyDTO);
                        ResidentialHouseHistoryDTO houseDto = residentialHouseHistoryService.createResidentialHouseHistory(houseHistoryDTO);
                        if (houseDto == null) {
                            return new ResponseEntity<>("Thông tin bất động sản đã tồn tại!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case 2:
                        ApartmentHistoryDTO apartmentHistoryDTO = new ApartmentHistoryDTO();
                        if (historyDTO.getRoomNumber() == null || historyDTO.getRoomNumber().isEmpty()) {
                            return new ResponseEntity<>("Số phòng không được để trống!", HttpStatus.BAD_REQUEST);
                        }
                        if (historyDTO.getBuildingName() == null || historyDTO.getBuildingName().isEmpty()) {
                            return new ResponseEntity<>("Tên tòa nhà không được để trống!", HttpStatus.BAD_REQUEST);
                        }
                        apartmentHistoryService.setDataToApartmentHistoryDTO(apartmentHistoryDTO, historyDTO);
                        ApartmentHistoryDTO dto = apartmentHistoryService.createApartmentHistory(apartmentHistoryDTO);
                        if (dto == null) {
                            return new ResponseEntity<>("Thông tin bất động sản đã tồn tại!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case 3:
                        ResidentialLandHistoryDTO landHistoryDTO = new ResidentialLandHistoryDTO();
                        residentialLandHistoryService.setDataToResidentialLandHistoryDTO(landHistoryDTO, historyDTO);
                        ResidentialLandHistoryDTO landDto = residentialLandHistoryService.createResidentialLandHistory(landHistoryDTO);
                        if (landDto == null) {
                            return new ResponseEntity<>("Thông tin bất động sản đã tồn tại!", HttpStatus.BAD_REQUEST);
                        }
                        break;
                }
                refundPercentDTO = refundPercentService.getActiveRefundPercent(2);
                refundMoney = spendMoney * (refundPercentDTO.getPercent()) / 100;
            }
            post.setStatus(new Status(3));
            post.setSpendMoney(spendMoney - refundMoney);
            postRepository.save(post);
            postService.changeStatusOfDerivativePostOfPost(postId);

            int userId = post.getUser().getId();
            DecimalFormat formatter = new DecimalFormat("###,###,###");
            String message = "Bạn được hoàn lại " + formatter.format(refundMoney) + " VNĐ";
            Pusher push = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
            push.setCluster("ap1");
            push.setEncrypted(true);
            push.trigger("my-channel-" + userId, "my-event", Collections.singletonMap("message", message));

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserId(userId);
            notificationDTO.setContent(message);
            notificationDTO.setPostId(postId);
            notificationDTO.setType("Refund");
            notificationService.createContactNotification(notificationDTO);

            User user = userRepository.getUserById(userId);
            int numberUnread = user.getUnreadNotification();
            numberUnread++;
            user.setUnreadNotification(numberUnread);
            long accountBalance = user.getAccountBalance();
            user.setAccountBalance(accountBalance + refundMoney);
            userRepository.save(user);

            List<Post> listPostDto = postRepository.getDerivativePostOfOriginalPost(postId);
            if (listPostDto.size() != 0) {
                for (Post p : listPostDto) {
                    User u = p.getUser();
                    String message1 = "Bài gốc bạn phái sinh đã hoàn thành giao dịch. Vui lòng đánh giá người đăng bài!";

                    push.setCluster("ap1");
                    push.setEncrypted(true);
                    push.trigger("my-channel-" + u.getId(), "my-event", Collections.singletonMap("message", message1));

                    NotificationDTO notification = new NotificationDTO();
                    notification.setUserId(u.getId());
                    notification.setContent(message1);
                    notification.setSender(userId);
                    notification.setType("FinishTransaction");
                    notificationService.createContactNotification(notification);

                    int unread = u.getUnreadNotification();
                    unread++;
                    u.setUnreadNotification(unread);
                    userRepository.save(u);
                }
            }

            return new ResponseEntity<>("Hoàn thành giao dịch!", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại hoặc không hoạt động!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/original/{userId}")
    public ResponseEntity<?> getAllOriginalPostOfUser(@PathVariable(name = "userId") int userId,
                                                      @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                      @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                                      @RequestHeader(name = "Authorization", required = false) String token,
                                                      @RequestParam(name = "propertyType", defaultValue = "0") String propertyType) {
        User user = userRepository.getUserById(userId);
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 6;
        SearchResponse searchResponse = new SearchResponse();
        if (user == null) {
            return new ResponseEntity<>("Người dùng không tồn tại!", HttpStatus.BAD_REQUEST);
        }
        if (user.isBlock()) {
            return new ResponseEntity<>("Người dùng không tồn tại!", HttpStatus.BAD_REQUEST);
        }
        if ((token == null) || (token.isEmpty())) {
            if (user.getRoles().size() == 2) {
                return new ResponseEntity<>("Không thể truy cập!", HttpStatus.BAD_REQUEST);
            } else {
                searchResponse = postService.getAllOriginalPostByUserId(userId, pageNumber, pageSize, sortValue, propertyType);

            }
        } else {
            int userToken = getUserIdFromToken(token);
            if (userToken == userId) {
                return new ResponseEntity<>("Người dùng không tồn tại!", HttpStatus.BAD_REQUEST);
            }
            User u = userRepository.getUserById(userToken);
            if (u.getCurrentRole() == 3) {
                searchResponse = postService.getOriginalPostByUserId(userId, pageNumber, pageSize, sortValue, propertyType);
            }
            if (u.getCurrentRole() == 2) {
                searchResponse = postService.getAllOriginalPostByUserId(userId, pageNumber, pageSize, sortValue, propertyType);
            }

        }
        Boolean isBroker = false;
        Set<Role> setRole = user.getRoles();
        Role role = roleRepository.findByName("BROKER").get();
        if (setRole.contains(role)) {
            isBroker = true;
        }
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        userDTO.setBroker(isBroker);
        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(user.getId(), 2);
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
    public ResponseEntity<?> getOutstandingPost() {
        List<SearchDTO> list = postService.getOutstandingPost();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/active-discount")
    public ResponseEntity<?> getActiveDiscount() {
        Map<String, Object> map = new HashMap<>();
        RefundPercentDTO refundPercentDTO = refundPercentService.getActiveRefundPercent(2);
        RefundPercentDTO dto = refundPercentService.getActiveRefundPercent(1);
        map.put("refundWithHistory", refundPercentDTO.getPercent());
        map.put("refundWithoutHistory", dto.getPercent());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
