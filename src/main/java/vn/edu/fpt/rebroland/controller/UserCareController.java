package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.entity.UserCare;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.UserCareRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ContactService;
import vn.edu.fpt.rebroland.service.UserCareDetailService;
import vn.edu.fpt.rebroland.service.UserCareService;
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

    private ContactService contactService;


    public UserCareController(UserCareService userCareService, UserRepository userRepository,
                              UserCareRepository userCareRepository, UserCareDetailService userCareDetailService,
                              ContactService contactService) {
        this.userCareService = userCareService;
        this.userRepository = userRepository;
        this.userCareRepository = userCareRepository;
        this.userCareDetailService = userCareDetailService;
        this.contactService = contactService;
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


    @PostMapping("/add-customer/{contactId}")
    @Transactional
    public ResponseEntity<?> createUserCareForBroker(@Valid @RequestBody UserCareDTO userCareDTO,
                                                     @PathVariable int contactId,
                                                     @RequestHeader(name = "Authorization") String token) {
//        try {
            int userId = getUserIdFromToken(token);
            int check = 0;
            User broker = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Broker", "id", userId));

            if (userCareDTO.getPostId() != null) {
                UserCare userCareWithUserCaredIdAndPostId = userCareRepository.findUserCareByUserCaredIdAndPostId(userCareDTO.getUserCaredId(), userCareDTO.getPostId());
                if (userCareWithUserCaredIdAndPostId == null) {
                    UserCare userCareWithOnlyUserCaredId = userCareRepository.findUserCareByUserCaredId(userCareDTO.getUserCaredId());
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
                    return new ResponseEntity<>("Đã chăm sóc người dùng này và bài đăng này", HttpStatus.OK);
                }
            } else {
                UserCare userCareWithOnlyUserCaredId = userCareRepository.findUserCareByUserCaredId(userCareDTO.getUserCaredId());
                if (userCareWithOnlyUserCaredId != null) {
                    contactService.deleteContact(contactId);
                    return new ResponseEntity<>("Đã chăm sóc người dùng này ", HttpStatus.OK);
                } else {
                    check = 3;
                    UserCareDTO newUserCareDTO = userCareService.createUserCare(userCareDTO, broker, null, check);
                    contactService.deleteContact(contactId);
                    return new ResponseEntity<>(newUserCareDTO, HttpStatus.CREATED);
                }
            }
//        }
//        catch (Exception e) {
//
//        }

//        return new ResponseEntity<>("Insert user care fail", HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/{careId}")
    @Transactional
    public ResponseEntity<?> createUserCareDetail(@PathVariable int careId,
                                                  @Valid @RequestBody UserCareDetailDTO userCareDetailDTO,
                                                  @RequestHeader(name = "Authorization") String token) {

        try {
            int userId = getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Broker", "id", userId));
            String appointmentDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (user.getCurrentRole() == 3) {
                if (userCareDetailDTO.getDateAppointment() != null && userCareDetailDTO.getTimeAppointment() != null ) {
                    appointmentDate = userCareDetailDTO.getDateAppointment() + " " + userCareDetailDTO.getTimeAppointment() + ":00";
                    Date date = new Date();
                    Date date1 = sdf.parse(appointmentDate);
                    if (date.compareTo(date1) > 0) {
                        return new ResponseEntity<>("you need change your appointmentTime !", HttpStatus.BAD_REQUEST);
                    } else {
                        UserCareDetailDTO userCareDetailDTO1 = userCareDetailService.createUserCareDetail(careId, userCareDetailDTO, date1);
                        if(userCareDetailDTO.getAlertTime()!=null){
                            sendRemindMessage(user.getPhone(), userCareDetailDTO.getDateAppointment(), userCareDetailDTO.getTimeAppointment(), userCareDetailDTO.getAlertTime());

                        }else{
                            sendRemindMessage(user.getPhone(), userCareDetailDTO.getDateAppointment(), userCareDetailDTO.getTimeAppointment(), 0);

                        }
                        return new ResponseEntity<>(userCareDetailDTO1, HttpStatus.CREATED);
                    }
                }
                if (userCareDetailDTO.getDateAppointment() == null && userCareDetailDTO.getTimeAppointment() == null && userCareDetailDTO.getAlertTime() == null) {
                    UserCareDetailDTO userCareDetailDTO1 = userCareDetailService.createUserCareDetail(careId, userCareDetailDTO, null);
                    return new ResponseEntity<>(userCareDetailDTO1, HttpStatus.CREATED);
                }

            } else {
                return new ResponseEntity<>("you need change to broker !", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Create fail !", HttpStatus.BAD_REQUEST);
    }

    public void sendRemindMessage(String phone, String dateAppointment, String timeAppointment, int alertTime) {
        try {
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String s = dateAppointment + " " + timeAppointment;
            Date appointmentDate = formater.parse(s);
            Calendar cal = Calendar.getInstance();
            cal.setTime(appointmentDate);
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
                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        int userId = getUserIdFromToken(token);
        int pageSize = 10;
        int pageNumber = Integer.parseInt(pageNo);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getCurrentRole() == 3) {
            CareResponse careResponse = userCareService.getUserCareByUserId(userId, keyword, pageNumber, pageSize);
            return new ResponseEntity<>(careResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Người dùng không phải broker! !", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("you need change to broker !", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("you need change to broker !", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("You need to change broker role!", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("You need to change broker role!", HttpStatus.OK);
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
            return new ResponseEntity<>("Finish transaction!!!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("You need to change broker role!", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{careId}")
    @Transactional
    public ResponseEntity<String> deleteUserCare(@PathVariable int careId,
                                                 @RequestHeader(name = "Authorization") String token) {
        int userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getCurrentRole() == 3) {
            userCareService.deleteRequiredWithUserCare(careId);
            return new ResponseEntity<>("Delete successfully !!!", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("You need to change customer role!", HttpStatus.BAD_REQUEST);
        }
    }

    public void sendSMS(String phone, String token) {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"),
                System.getenv("TWILIO_AUTH_TOKEN"));

        Message.creator(new PhoneNumber(phone.replaceFirst("0", "+84")),
                new PhoneNumber("+19844647230"), token).create();
    }

}