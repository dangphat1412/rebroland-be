package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.*;
import vn.edu.fpt.rebroland.service.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.cloudinary.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/user-care")
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
public class UserCareController {
    private UserCareService userCareService;

    private UserRepository userRepository;

    private UserCareRepository userCareRepository;

    private UserCareDetailService userCareDetailService;

    private UserCareDetailRepository userCareDetailRepository;

    private ContactService contactService;

    private ContactRepository contactRepository;

    private UserService userService;
    private RoleRepository roleRepository;

    private PostService postService;

    private ResidentialLandService residentialLandService;
    private ResidentialHouseService residentialHouseService;
    private ApartmentService apartmentService;

    public UserCareController(UserCareService userCareService, UserRepository userRepository, UserCareRepository userCareRepository,
                              UserCareDetailService userCareDetailService, UserCareDetailRepository userCareDetailRepository,
                              ContactService contactService, ContactRepository contactRepository, UserService userService,
                              RoleRepository roleRepository, PostService postService, ResidentialLandService residentialLandService,
                              ResidentialHouseService residentialHouseService, ApartmentService apartmentService) {
        this.userCareService = userCareService;
        this.userRepository = userRepository;
        this.userCareRepository = userCareRepository;
        this.userCareDetailService = userCareDetailService;
        this.userCareDetailRepository = userCareDetailRepository;
        this.contactService = contactService;
        this.contactRepository = contactRepository;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.postService = postService;
        this.residentialLandService = residentialLandService;
        this.residentialHouseService = residentialHouseService;
        this.apartmentService = apartmentService;
    }

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

    @GetMapping("/detail-post/{postId}")
    public ResponseEntity<?> getDetailPost(@PathVariable int postId,
                                           @RequestHeader(name = "Authorization") String token) {
        RealEstatePostDTO realEstatePostDTO = new RealEstatePostDTO();
        int userId = getUserIdFromToken(token);
        PostDTO postDTO = postService.findPostByPostId(postId);
        if(userId != postDTO.getUser().getId()){
            return new ResponseEntity<>("Bạn không phải là chủ bài viết này.", HttpStatus.BAD_REQUEST);
        }
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
//            List<BrokerInfoOfPostDTO> list = postService.getDerivativePostOfOriginalPost(postId);
//            Map<String, Object> map = new HashMap<>();
//            map.put("post", realEstatePostDTO);
//            map.put("brokers", list);
            return new ResponseEntity<>(realEstatePostDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/add-customer/{contactId}")
    @Transactional
    public ResponseEntity<?> createUserCareForBroker(@PathVariable int contactId,
                                                     @RequestHeader(name = "Authorization") String token) {
//
        int userId = getUserIdFromToken(token);
        int check = 0;
        User broker = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Broker", "id", userId));
        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact", "id", contactId));
        int userRequestId = contact.getUserRequestId();
        User userRequest = userRepository.getUserById(userRequestId);
        if((userRequest == null) || (userRequest.isBlock())){
            contactService.deleteContact(contactId);
            return new ResponseEntity<>("Người dùng không tồn tại hoặc đã bị chặn!", HttpStatus.BAD_REQUEST);
        }
        UserCareDTO userCareDTO = new UserCareDTO();
        userCareDTO.setUserCaredId(contact.getUserRequestId());
        if (contact.getPost() == null) {
            userCareDTO.setPostId(null);
        } else {
            userCareDTO.setPostId(contact.getPost().getPostId());
        }
        if (userCareDTO.getPostId() != null) {
            UserCare userCareWithUserCaredIdAndPostId = userCareRepository.findUserCareByUserCaredIdAndPostId(userCareDTO.getUserCaredId(), userCareDTO.getPostId(), userId);
            if (userCareWithUserCaredIdAndPostId == null) {
                UserCare userCareWithOnlyUserCaredId = userCareRepository.findUserCareByUserCaredId(userCareDTO.getUserCaredId(), userId);
                if (userCareWithOnlyUserCaredId != null) {
                    check = 1;
                } else {
                    check = 2;
                }
                if (broker.getRoles().size() == 2) {
                    UserCareDTO newUserCareDTO = userCareService.createUserCare(userCareDTO, broker, userCareWithOnlyUserCaredId, check);
                    contactService.deleteContact(contactId);
                    return new ResponseEntity<>(newUserCareDTO, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("Người dùng không phải người môi giới", HttpStatus.BAD_REQUEST);
                }
            } else {
                contactService.deleteContact(contactId);
                return new ResponseEntity<>("Đã chăm sóc người dùng này và bài đăng này", HttpStatus.CREATED);
            }
        } else {
            UserCare userCareWithOnlyUserCaredId = userCareRepository.findUserCareByUserCaredId(userCareDTO.getUserCaredId(), userId);
            if (userCareWithOnlyUserCaredId != null) {
                contactService.deleteContact(contactId);
                return new ResponseEntity<>("Đã chăm sóc người dùng này ", HttpStatus.CREATED);
            } else {
                check = 3;
                UserCareDTO newUserCareDTO = userCareService.createUserCare(userCareDTO, broker, null, check);
                contactService.deleteContact(contactId);
                return new ResponseEntity<>(newUserCareDTO, HttpStatus.CREATED);
            }
        }
//

    }

    @GetMapping("/add-customer/get-info/{phone}")
    public ResponseEntity<?> checkCustomerPhone(@PathVariable(name = "phone") String phone,
                                                @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);

        User userCared = userRepository.getUserByPhone(phone);

        if (userCared != null) {
            Role role = roleRepository.findByName("ADMIN").get();
            if (userCared.getRoles().contains(role)) {
                return new ResponseEntity<>("Số điện thoại không hợp lệ!", HttpStatus.BAD_REQUEST);
            }
            if (userCared.getId() == userId) {
                return new ResponseEntity<>("Người dùng không thể chăm sóc chính mình!", HttpStatus.BAD_REQUEST);
            }
            UserCare userCare = userCareRepository.getUserCareByUserIdAndStatusFalse(userId, userCared.getId());
            if (userCare != null) {
                return new ResponseEntity<>("Số điện thoại này đang được chăm sóc!", HttpStatus.BAD_REQUEST);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("id", userCared.getId());
                map.put("fullName", userCared.getFullName());
                map.put("phone", userCared.getPhone());
                map.put("email", userCared.getEmail());
                map.put("avatar", userCared.getAvatar());
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("Số điện thoại không tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-usercare/{userCaredId}")
    public ResponseEntity<?> createUserCare(@PathVariable(name = "userCaredId") int userCaredId,
                                            @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        UserDTO userDTO = userService.getUserById(userId);

        User userCared = userRepository.getUserById(userCaredId);

        UserCare userCareWithOnlyUserCaredId = userCareRepository.findUserCareByUserCaredId(userCaredId, userId);
        if (userCareWithOnlyUserCaredId != null) {
            return new ResponseEntity<>("Người dùng đã ở trong danh sách chăm sóc!", HttpStatus.BAD_REQUEST);
        }

        if (userCaredId == userId) {
            return new ResponseEntity<>("Người dùng không thể chăm sóc chính mình!", HttpStatus.BAD_REQUEST);
        }
        if (userCared != null) {
            Role role = roleRepository.findByName("ADMIN").get();
            if (userCared.getRoles().contains(role)) {
                return new ResponseEntity<>("Số điện thoại không hợp lệ!", HttpStatus.BAD_REQUEST);
            }
            UserCareDTO dto = new UserCareDTO();
            dto.setUserCaredId(userCaredId);
            UserDTO userCaredDTO = userService.getUserById(userCaredId);
            dto.setUser(userDTO);

            UserCareDTO userCareDTO = userCareService.createNewUserCare(dto, userCaredDTO);

            return new ResponseEntity<>(userCareDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Người dùng không tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/{careId}")
    @Transactional
    public ResponseEntity<?> createUserCareDetail(@PathVariable int careId,
                                                  @Valid @RequestBody UserCareDetailDTO userCareDetailDTO,
                                                  @RequestHeader(name = "Authorization") String token) {

        try {
            int userId = getUserIdFromToken(token);
            UserCare userCare = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Broker", "id", userId));
            String appointmentDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            if (user.getCurrentRole() == 3) {
                if (userCare.isStatus()) {
                    return new ResponseEntity<>("Việc chăm sóc khách hàng đã kết thúc.", HttpStatus.BAD_REQUEST);
                }
                if ((userCareDetailDTO.getDateAppointment() != null) && (userCareDetailDTO.getTimeAppointment() != null)) {
                    appointmentDate = userCareDetailDTO.getDateAppointment() + " " + userCareDetailDTO.getTimeAppointment() + ":00";
                    Date date = new Date();
                    Date date1 = sdf.parse(appointmentDate);
                    if (date.compareTo(date1) > 0) {
                        return new ResponseEntity<>("Thời gian hẹn trước không đúng !", HttpStatus.BAD_REQUEST);
                    } else {
                        if (userCareDetailDTO.getAlertTime() != null) {
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                            cal.setTime(date1);
                            cal.add(Calendar.HOUR, -7);
                            date1 = cal.getTime();
                            cal.add(Calendar.SECOND, -userCareDetailDTO.getAlertTime() * 60);
                            Date dateAlert = cal.getTime();

                            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                            c.setTime(date);
                            cal.add(Calendar.HOUR, -7);
                            date = c.getTime();
                            if (date.compareTo(dateAlert) > 0) {
                                return new ResponseEntity<>("Thời gian hẹn trước không đúng !", HttpStatus.BAD_REQUEST);
                            }
                            sendRemindMessage(user.getPhone(), userCareDetailDTO.getDateAppointment(), userCareDetailDTO.getTimeAppointment(), userCareDetailDTO.getAlertTime() * 60);

                        } else {
                            sendRemindMessage(user.getPhone(), userCareDetailDTO.getDateAppointment(), userCareDetailDTO.getTimeAppointment(), 0);

                        }
                        UserCareDetailDTO userCareDetailDTO1 = userCareDetailService.createUserCareDetail(careId, userCareDetailDTO, date1);
                        return new ResponseEntity<>(userCareDetailDTO1, HttpStatus.CREATED);
                    }
                }
                if (userCareDetailDTO.getDateAppointment() == null && userCareDetailDTO.getTimeAppointment() == null && userCareDetailDTO.getAlertTime() == null) {
                    UserCareDetailDTO userCareDetailDTO1 = userCareDetailService.createUserCareDetail(careId, userCareDetailDTO, null);
                    return new ResponseEntity<>(userCareDetailDTO1, HttpStatus.CREATED);
                }

            } else {
                return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Create fail !", HttpStatus.BAD_REQUEST);
    }

    public void sendRemindMessage(String phone, String dateAppointment, String timeAppointment, int alertTime) {
        try {
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String s = dateAppointment + " " + timeAppointment + ":00";
            Date appointmentDate = formater.parse(s);
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            cal.setTime(appointmentDate);
            cal.add(Calendar.HOUR, -7);
            cal.add(Calendar.SECOND, -alertTime);
            Date dateAlert = cal.getTime();

            String message = "Hôm nay bạn có hẹn vào lúc " + appointmentDate;

            setTimer(dateAlert, phone, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimer(Date dateAlert, String phone, String message) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
            sendSMS(phone, message);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, dateAlert);
    }


    @GetMapping
    public ResponseEntity<?> getUserCareByUserId(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                 @RequestHeader(name = "Authorization") String token,
                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                 @RequestParam(name = "status", defaultValue = "0") String status) {

        int userId = getUserIdFromToken(token);
        int pageSize = 5;
        int pageNumber = Integer.parseInt(pageNo);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getCurrentRole() == 3) {
            CareResponse careResponse = userCareService.getUserCareByUserId(userId, keyword, status, pageNumber, pageSize);
            return new ResponseEntity<>(careResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{careId}")
    public ResponseEntity<?> getDetailUserCare(@PathVariable int careId,
                                               @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getCurrentRole() == 3) {
            UserCareDTO userCare = userCareService.getUserCareByCareId(careId);

            return new ResponseEntity<>(userCare, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/details/{careId}")
    public ResponseEntity<?> getListUserCareDetails(@PathVariable int careId,
                                                    @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getCurrentRole() == 3) {
            List<CareDetailResponse> userCareDetailDTOList = userCareDetailService.getListUserCareByCareId(careId);
            UserCareDTO userCareDTO = userCareService.getUserCareByCareId(careId);
            List<ShortPostDTO> listPostCare = userCareService.getPostCareByCareId(careId);

            Map<String, Object> map = new HashMap<>();
            map.put("timeline", userCareDetailDTOList);
            map.put("user", userCareDTO);
            map.put("posts", listPostCare);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
        }
    }


    // accept demand from customer
    @PutMapping("/{careId}")
    @Transactional
    public ResponseEntity<?> updateUserCare(@PathVariable int careId,
                                            @Valid @RequestBody UserCareDTO userCareDTO,
                                            @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getCurrentRole() == 3) {
            UserCareDTO newCareDTO = userCareService.updateUserCare(userCareDTO, careId);
            return new ResponseEntity<>("Cập nhật thành công !", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/details/{detailId}")
    @Transactional
    public ResponseEntity<?> updateUserCare(@PathVariable int detailId,
                                            @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getCurrentRole() == 3) {
            UserCareDetailDTO userCareDetailDTO = userCareDetailService.updateUserCareDetail(detailId);
            return new ResponseEntity<>(userCareDetailDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/finish/{careId}")
    @Transactional
    public ResponseEntity<String> finishTransaction(@PathVariable int careId,
                                                    @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getCurrentRole() == 3) {
            UserCareDTO newCareDTO = userCareService.finishTransactionUserCare(careId);
            return new ResponseEntity<>("Kết thúc chăm sóc khách hàng", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ nhà môi giới", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{careId}")
    @Transactional
    public ResponseEntity<String> deleteUserCare(@PathVariable int careId,
                                                 @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserCare care = userCareRepository.findById(careId).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", careId));
        if (user.getCurrentRole() == 3) {
            userCareService.deleteRequiredWithUserCare(careId);
            return new ResponseEntity<>("Delete successfully !!!", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("You need to change customer role!", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/detail/{detailId}")
    @Transactional
    public ResponseEntity<String> deleteUserCareDetail(@PathVariable(name = "detailId") int detailId,
                                                       @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserCareDetail userCareDetail = userCareDetailRepository.findById(detailId).orElseThrow(() -> new ResourceNotFoundException("Detail", "id", detailId));
        UserCare userCare = userCareRepository.findById(userCareDetail.getUserCare().getCareId()).orElseThrow(() -> new ResourceNotFoundException("UserCare", "id", userCareDetail.getUserCare().getCareId()));

        if (user.getCurrentRole() == 3) {
            if (userCare.isStatus()) {
                return new ResponseEntity<>("Bạn không thể xóa lịch trình này.", HttpStatus.BAD_REQUEST);
            } else {
                userCareService.deleteUserCareDetailById(detailId);
                return new ResponseEntity<>("Xóa lịch thành công.", HttpStatus.NO_CONTENT);
            }
        } else {
            return new ResponseEntity<>("Bạn phải chuyển sang chế độ môi giới", HttpStatus.BAD_REQUEST);
        }
    }

    public void sendSMS(String phone, String token) {
        Twilio.init("ACd79616329e4784b2208b5f134088905d",
                "dc59c60fcf2b169f0b4fc37fbb8da680");

        Message.creator(new PhoneNumber(phone.replaceFirst("0", "+84")),
                new PhoneNumber("+19844647230"), token).create();
    }

}