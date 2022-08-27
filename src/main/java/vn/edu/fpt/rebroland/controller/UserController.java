package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.security.JwtTokenProvider;
import vn.edu.fpt.rebroland.service.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
public class UserController {
    private UserService userService;

    private ImageService imageService;

    private ModelMapper mapper;

    private PostService postService;

    private UserFollowPostService followPostService;

    private RoleRepository roleRepository;
    private PriceService priceService;
    private PaymentService paymentService;

    private BrokerInfoService brokerInfoService;

    public UserController(UserService userService, ImageService imageService, ModelMapper mapper, PostService postService, UserFollowPostService followPostService,
                          RoleRepository roleRepository, PriceService priceService, PaymentService paymentService, BrokerInfoService brokerInfoService) {
        this.userService = userService;
        this.imageService = imageService;
        this.mapper = mapper;
        this.postService = postService;
        this.followPostService = followPostService;
        this.roleRepository = roleRepository;
        this.priceService = priceService;
        this.paymentService = paymentService;
        this.brokerInfoService = brokerInfoService;
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private OtpService otpService;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            User user = userRepository.findByPhone(loginDTO.getPhone()).
                    orElseThrow(() -> new UsernameNotFoundException("Không tìm được người dùng có số điện thoại là: " + loginDTO.getPhone()));
//            Role role = roleRepository.findByName("ADMIN").get();
            if(user.isBlock() == false){
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginDTO.getPhone(), loginDTO.getPassword()
                ));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = tokenProvider.generateToken(authentication);
//                boolean isAdmin = true;
//                if(user.getRoles().contains(role)){
//                    return new ResponseEntity<>(new JWTAuthResponse(token, isAdmin) , HttpStatus.OK);
//                }else{
//                    return new ResponseEntity<>(new JWTAuthResponse(token), HttpStatus.OK);
//                }
                return new ResponseEntity<>(new JWTAuthResponse(token), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Số điện thoại đã bị chặn !", HttpStatus.BAD_REQUEST);
            }
        }catch (AuthenticationException e){
            return new ResponseEntity<>("Số điện thoại hoặc mật khẩu không đúng!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/signup", consumes = {"*/*"})
    public ResponseEntity<?> preRegister(@Valid @RequestBody RegisterDTO registerDTO){
        try {
            if(userRepository.existsByPhone(registerDTO.getPhone())) {
                return new ResponseEntity<>("Số điện thoại này đã được sử dụng!", HttpStatus.BAD_REQUEST);
            }

            String token = otpService.generateOtp(registerDTO.getPhone()) + "";
            otpService.remainCount(registerDTO.getPhone(), 3);

//            sendSMS(registerDTO.getPhone(), token);
            String fullName = registerDTO.getFullName().trim();
            registerDTO.setFullName(fullName);
            Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
            if (pattern.matcher(registerDTO.getFullName()).find()) {
                return new ResponseEntity<>("Họ và tên không chứa ký tự đặc biệt và số!", HttpStatus.BAD_REQUEST);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("user", registerDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES);
            map.put("remainTime", 3);
            map.put("otp", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch (AuthenticationException e){
            return new ResponseEntity<>("Đăng ký thất bại!", HttpStatus.BAD_REQUEST);
        }
    }

    //decode token
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    @GetMapping
    public ResponseEntity<?> getUserByPhone(@RequestHeader(name = "Authorization") String token){
        String[] parts = token.split("\\.");
//        JSONObject header = new JSONObject(decode(parts[0]));
        JSONObject payload = new JSONObject(decode(parts[1]));
//        String signature = decode(parts[2]);
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        Boolean isBroker = false;
        Set<Role> setRole = user.getRoles();
        Role role = roleRepository.findByName("BROKER").get();
        if(setRole.contains(role)){
            isBroker = true;
        }

        UserDTO userDTO = mapper.map(user, UserDTO.class);


        List<ShortPostDTO> listEntity = followPostService.getShortFollowPostByUser(phone);

        Map<String, Object> map = new HashMap<>();
        map.put("user", userDTO);
        map.put("following", listEntity);
        map.put("isBroker", isBroker);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/signup/otp")
    public ResponseEntity<?> processRegister(@RequestBody RegisterDTO registerDTO){
        if(userRepository.existsByPhone(registerDTO.getPhone())) {
            return new ResponseEntity<>("Số điên thoại đã được sử dụng!", HttpStatus.BAD_REQUEST);
        }
        int remainTime = 3;
        if(otpService.getCount(registerDTO.getPhone()) != null){
            remainTime = otpService.getCount(registerDTO.getPhone());
        }
        if(remainTime == 0){
            otpService.clearCount(registerDTO.getPhone());
            otpService.clearOtp(registerDTO.getPhone());
            return new ResponseEntity<>("Nhập sai quá số lần quy định!", HttpStatus.BAD_REQUEST);
        }
        if((registerDTO.getToken() == null) || (registerDTO.getToken().isEmpty())){
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }
        if(otpService.getOtp(registerDTO.getPhone()) == null){
            return new ResponseEntity<>("Mã OTP đã hết hạn!", HttpStatus.BAD_REQUEST);
        }

        int otp = Integer.parseInt(registerDTO.getToken());

        if (otp != otpService.getOtp(registerDTO.getPhone())) {
            otpService.remainCount(registerDTO.getPhone(), remainTime);
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }
        userService.createUser(registerDTO);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                registerDTO.getPhone(), registerDTO.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        otpService.clearOtp(registerDTO.getPhone());
        return new ResponseEntity<>(new JWTAuthResponse(token), HttpStatus.OK);
    }

    @PostMapping("/forgot-password/otp")
    public ResponseEntity<?> processForgotPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
//        User user = userRepository.findByPhone(resetPasswordDTO.getPhone()).
//                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + resetPasswordDTO.getPhone()));
        User user = userRepository.getUserByPhone(resetPasswordDTO.getPhone());
//        if(resetPasswordDTO.getToken() == null){
//            return new ResponseEntity<>("Mã OTP không được để trống!", HttpStatus.BAD_REQUEST);
//        }
        if (user != null) {
            String token = otpService.generateOtp(resetPasswordDTO.getPhone()) + "";
            otpService.remainCount(resetPasswordDTO.getPhone(), 3);
//            sendSMS(resetPasswordDTO.getPhone(), token);
            Map<String, Object> map = new HashMap<>();
            map.put("user", resetPasswordDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES);
            map.put("remainTime", 3);
            map.put("otp", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Số điện thoại chưa được đăng kí!", HttpStatus.BAD_REQUEST);
        }
//        return new ResponseEntity<>("Gửi OTP thất bại!", HttpStatus.BAD_REQUEST);


    }

    public void sendSMS(String phone, String token) {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"),
                System.getenv("TWILIO_AUTH_TOKEN"));

        Message.creator(new PhoneNumber(phone.replaceFirst("0","+84")),
                new PhoneNumber("+19844647230"), token).create();
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> processResetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
//        User user = userRepository.findByPhone(resetPasswordDTO.getPhone()).
//                orElseThrow(() -> new UsernameNotFoundException("Số điện thoại chưa được đăng kí " + resetPasswordDTO.getPhone()));
        User user = userRepository.getUserByPhone(resetPasswordDTO.getPhone());

        if(user == null){
            return new ResponseEntity<>("Số điện thoại chưa được đăng kí!", HttpStatus.BAD_REQUEST);
        }
        int remainTime = 3;
        if(otpService.getCount(resetPasswordDTO.getPhone()) != null){
            remainTime = otpService.getCount(resetPasswordDTO.getPhone());
        }
        if(remainTime == 0){
            otpService.clearCount(resetPasswordDTO.getPhone());
            otpService.clearOtp(resetPasswordDTO.getPhone());
            return new ResponseEntity<>("Nhập sai quá số lần quy định!", HttpStatus.BAD_REQUEST);
        }
        if(otpService.getOtp(resetPasswordDTO.getPhone()) == null){
            return new ResponseEntity<>("Mã OTP đã hết hạn!", HttpStatus.BAD_REQUEST);
        }

        if (resetPasswordDTO.getToken() != otpService.getOtp(resetPasswordDTO.getPhone())) {
            otpService.remainCount(resetPasswordDTO.getPhone(), remainTime);
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }else {

            userService.updatePassword(user, resetPasswordDTO.getPassword());

            return new ResponseEntity<>("Đổi mật khẩu thành công!", HttpStatus.OK);


        }

    }

    @PostMapping("/broker/signup/{priceId}")
    @Transactional
    public ResponseEntity<?> createBroker(@RequestHeader(name = "Authorization") String token,
                                          @PathVariable(name = "priceId") int priceId){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        PriceDTO newPriceDTO = priceService.getPriceBroker(priceId);
        long accountBalance = user.getAccountBalance();
        long price = newPriceDTO.getPrice() * (100 - newPriceDTO.getDiscount()) / 100;
        if(accountBalance >= price){
            Role role = roleRepository.findByName("BROKER").get();
            //ko co role broker
            if(!user.getRoles().contains(role)){
                //payment
                UserDTO userDTO = userService.createBroker(user, role);
                if(userDTO != null){
                    user.setAccountBalance(accountBalance - price);
                    userRepository.save(user);

                    TransactionDTO transactionDTO = new TransactionDTO();
                    transactionDTO.setUser(userDTO);
                    transactionDTO.setDescription("Đăng ký broker");
                    transactionDTO.setAmount(price);
                    transactionDTO.setTypeId(2);
                    paymentService.createTransaction(transactionDTO);

                    BrokerInfoDTO brokerInfoDTO = new BrokerInfoDTO();
                    brokerInfoDTO.setUserId(userDTO.getId());

                    long millis = System.currentTimeMillis();
                    java.sql.Date dateNow = new java.sql.Date(millis);
                    Calendar c = Calendar.getInstance();
                    c.setTime(dateNow);
                    c.add(Calendar.DAY_OF_MONTH, newPriceDTO.getUnitDate());
                    brokerInfoDTO.setEndDate(c.getTime());
                    brokerInfoService.createBrokerInfo(brokerInfoDTO);

                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>("Tạo tài khoản thất bại !", HttpStatus.BAD_REQUEST);
                }

            }
            return new ResponseEntity<>("Người dùng đã có tài khoản broker!", HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>("Số tiền trong tài khoản không đủ!", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/switch")
    public ResponseEntity<?> switchRole(@RequestHeader(name = "Authorization") String token){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

        //Phai bo sung dieu kien payment
        Boolean isBroker = true;
        if(user.getRoles().size() == 2 ){
            int roleId = user.getCurrentRole();

            if(roleId == 2){
                user.setCurrentRole(3);
                userRepository.save(user);
            }else{
                user.setCurrentRole(2);
                userRepository.save(user);
            }
            UserDTO userDTO = mapper.map(user, UserDTO.class);
            Map<String, Object> map = new HashMap<>();
            map.put("user", userDTO);
            map.put("isBroker", isBroker);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{

            return new ResponseEntity<>("Người dùng chỉ có role User!", HttpStatus.BAD_REQUEST);

        }
    }

    //list broker
    @GetMapping("/broker")
    public ResponseEntity<?> getAllBroker(@RequestParam(name = "keyword", required = false) String fullName,
                                          @RequestParam(name = "ward", required = false) String ward,
                                          @RequestParam(name = "district", required = false) String district,
                                          @RequestParam(name = "province", required = false) String province,
                                          @RequestParam(name = "propertyTypes", required = false) List<String> listPropertyType,
                                          @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                          @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                          @RequestHeader(name = "Authorization", required = false) String token){
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 8;
        try{
//            List<UserDTO> listBroker = userService.getAllBrokerPaging(pageNumber, pageSize, user.getId());
            int userId = 0;
            if(token != null){
                String[] parts = token.split("\\.");
                JSONObject payload = new JSONObject(decode(parts[1]));
                String phone = payload.getString("sub");
                User user = userRepository.findByPhone(phone).
                        orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));

                userId = user.getId();
            }
            List<UserDTO> listBroker = userService.searchBroker(fullName, ward, district, province, listPropertyType, pageNumber, pageSize, sortValue, userId);

            List<UserDTO> listAllBroker = userService.getAllBroker(fullName, ward, district, province, listPropertyType, userId);
            int totalResult = listAllBroker.size();

            int totalPage = 0;
            if(listAllBroker.size() % pageSize == 0){
                totalPage = listAllBroker.size() / pageSize;
            }else{
                totalPage = (listAllBroker.size() / pageSize) + 1;
            }
//            if(totalPage < pageNumber + 1){
//                return new ResponseEntity<>("Không tìm thấy kết quả phù hợp!", HttpStatus.OK);
//            }
            Map<String, Object> map = new HashMap<>();
            map.put("list", listBroker);
            map.put("pageNo", pageNumber+1);
            map.put("totalPages", totalPage);
            map.put("totalResult", totalResult);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Không tồn tại trang!", HttpStatus.OK);
        }


    }

//    @GetMapping("/user-detail/{userId}")
//    public ResponseEntity<?> getDetailUser(@PathVariable(name = "userId") String userId,
//                                           @RequestParam(name = "propertyType", required = false) String type,
//                                           @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
//        int id = Integer.parseInt(userId);
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
//        int pageNumber = Integer.parseInt(pageNo);
//        int pageSize = 8;
//        SearchResponse listPost = postService.getPostByUserId(pageNumber, pageSize, id, type);
//
//        UserDTO userDTO = mapper.map(user, UserDTO.class);
//
//        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(user.getId(), user.getCurrentRole());
//        if(avgRate != null){
//            userDTO.setAvgRate(avgRate.getAvgRate());
//        }else{
//            userDTO.setAvgRate(0);
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("user", userDTO);
//        map.put("listPost", listPost);
//        return new ResponseEntity<>(map, HttpStatus.OK);
//    }

    @PutMapping()
    public ResponseEntity<String> updateUser(@RequestHeader(name = "Authorization") String token,
                                            @Valid @RequestBody UserDTO userDTO){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-1234567890]");
        if (pattern.matcher(userDTO.getFullName()).find()) {
            return new ResponseEntity<>("Tên không chứa ký tự đặc biệt và số", HttpStatus.BAD_REQUEST);
        }
        int updateStatus = userService.updateUser(phone, userDTO);
        if(updateStatus == 1){
            return new ResponseEntity<>("Chỉnh sửa thông tin cá nhân thành công!", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Chỉnh sửa thông tin cá nhân thất bại!", HttpStatus.BAD_REQUEST);
        }
    }

    //search broker
//    @GetMapping("/search")
//    public ResponseEntity<?> searchBroker(@RequestParam(name = "name", required = false) String fullName,
//                                          @RequestParam(name = "ward", required = false) String ward,
//                                          @RequestParam(name = "district", required = false) String district,
//                                          @RequestParam(name = "province", required = false) String province,
//                                          @RequestParam(name = "address", required = false) String address,
//                                          @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
//                                          @RequestParam(name = "sortValue", defaultValue = "0") String sortValue){
//        int pageNumber = Integer.parseInt(pageNo);
//        int pageSize = 5;
//        List<UserDTO> listUserDto = userService.searchBroker(fullName, ward, district, province, address, pageNumber, pageSize, sortValue);
//
//        return new ResponseEntity<>(listUserDto, HttpStatus.OK);
//    }

    //change password
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader(name = "Authorization") String token,
                                            @Valid @RequestBody ChangePasswordDTO changePasswordDTO){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        int change = userService.changePassword(user, changePasswordDTO);
        if(change == 0){
            return new ResponseEntity<>("Mật khẩu mới không được trùng với mật khẩu cũ!", HttpStatus.BAD_REQUEST);
        }
        if(change == 2){
            return new ResponseEntity<>("Mật khẩu cũ không đúng !", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Đổi mật khẩu thành công!", HttpStatus.CREATED);

    }

    @GetMapping("/broker/price")
    public ResponseEntity<?> getPriceBroker(@RequestHeader(name = "Authorization") String token){
        List<PriceDTO> priceDTO = priceService.getListPriceBroker(2);
        return new ResponseEntity<>(priceDTO, HttpStatus.OK);
    }

    @PostMapping("/change-phone/send-otp")
    public ResponseEntity<?> updatePhone(@RequestHeader(name = "Authorization") String token,
                                         @Valid @RequestBody PhoneDTO registerDTO){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        //check admin phone
        Role roles = roleRepository.findByName("ADMIN").get();
        User receiver = userRepository.getUserByPhone(registerDTO.getPhone());
        if(receiver != null && receiver.getRoles().contains(roles)){
            return new ResponseEntity<>("Số điện thoại không hợp lệ!", HttpStatus.BAD_REQUEST);
        }
        if(user.getPhone().equals(registerDTO.getPhone())){
            return new ResponseEntity<>("SĐT cập nhật trùng với SĐT hiện tại !", HttpStatus.BAD_REQUEST);
        }else{
            String otp = otpService.generateOtp(registerDTO.getPhone()) + "";
            otpService.remainCount(registerDTO.getPhone(), 3);

//                sendSMS(registerDTO.getPhone(), otp);
            Map<String, Object> map = new HashMap<>();
            map.put("phoneData", registerDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES);
            map.put("remainTime", 3);
            map.put("otp", otp);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/change-phone")
    @Transactional
    public ResponseEntity<?> changePhoneNumber(@RequestHeader(name = "Authorization") String token,
                                                @Valid @RequestBody PhoneDTO registerDTO){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User newUser = userRepository.getUserByPhone(phone);
        if(newUser == null){
            return new ResponseEntity<>("Số điện thoại không tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
        }
        int remainTime = 3;
        if(otpService.getCount(registerDTO.getPhone()) != null){
            remainTime = otpService.getCount(registerDTO.getPhone());
        }
        if(remainTime == 0){
            otpService.clearCount(registerDTO.getPhone());
            otpService.clearOtp(registerDTO.getPhone());
            return new ResponseEntity<>("Nhập sai quá số lần quy định!", HttpStatus.BAD_REQUEST);
        }
        if(otpService.getOtp(registerDTO.getPhone()) == null){
            return new ResponseEntity<>("Mã OTP hết hạn!", HttpStatus.BAD_REQUEST);
        }
        if((registerDTO.getToken() == null) || (registerDTO.getToken().isEmpty())){
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }
        int otp = Integer.parseInt(registerDTO.getToken());

        if (otp != otpService.getOtp(registerDTO.getPhone())) {
            otpService.remainCount(registerDTO.getPhone(), remainTime);
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }else{
            UserDTO userDTO = userService.getUserByPhone(registerDTO.getPhone());
            if(userDTO != null){
                User user = userRepository.findByPhone(registerDTO.getPhone()).
                        orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + registerDTO.getPhone()));;
                user.setBlock(true);
                userRepository.save(user);
                //inactive all post of user
                postService.blockAllPostByUserId(user.getId());

                newUser.setPhone(registerDTO.getPhone());
                userRepository.save(newUser);

                String newToken = tokenProvider.generateToken(newUser.getPhone());
                return new ResponseEntity<>(new JWTAuthResponse(newToken), HttpStatus.OK);
//                return new ResponseEntity<>("Cập nhật SĐT thành công!", HttpStatus.OK);
            }else{
                newUser.setPhone(registerDTO.getPhone());
                userRepository.save(newUser);
//                return new ResponseEntity<>("Cập nhật SĐT thành công !", HttpStatus.OK);

                String newToken = tokenProvider.generateToken(newUser.getPhone());
                return new ResponseEntity<>(new JWTAuthResponse(newToken), HttpStatus.OK);
            }

        }

    }
}
