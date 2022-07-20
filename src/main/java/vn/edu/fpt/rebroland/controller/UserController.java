package vn.edu.fpt.rebroland.controller;

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
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.security.JwtTokenProvider;
import vn.edu.fpt.rebroland.service.*;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private UserService userService;

    private ImageService imageService;

    private ModelMapper mapper;

    private PostService postService;

    private UserFollowPostService followPostService;

    private RoleRepository roleRepository;

    public UserController(UserService userService, ImageService imageService, ModelMapper mapper, PostService postService, UserFollowPostService followPostService,
                          RoleRepository roleRepository) {
        this.userService = userService;
        this.imageService = imageService;
        this.mapper = mapper;
        this.postService = postService;
        this.followPostService = followPostService;
        this.roleRepository = roleRepository;
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

            if(user.isBlock() == false){
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginDTO.getPhone(), loginDTO.getPassword()
                ));

                SecurityContextHolder.getContext().setAuthentication(authentication);


                String token = tokenProvider.generateToken(authentication);
                return new ResponseEntity<>(new JWTAuthResponse(token), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Số điện thoại đã bị chặn !", HttpStatus.OK);
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
            sendSMS(registerDTO.getPhone(), token);
//            registerDTO.setToken(token);
            Map<String, Object> map = new HashMap<>();
            map.put("user", registerDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES);

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
//        String token = otpService.generateOtp(registerDTO.getPhone()) + "";
//        sendSMS(registerDTO.getPhone(), token);

        int otp = Integer.parseInt(registerDTO.getToken());

        if (otp != otpService.getOtp(registerDTO.getPhone())) {
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
        User user = userRepository.findByPhone(resetPasswordDTO.getPhone()).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + resetPasswordDTO.getPhone()));
        if (user != null) {
            String token = otpService.generateOtp(resetPasswordDTO.getPhone()) + "";
//            sendSMS(resetPasswordDTO.getPhone(), token);
            Map<String, Object> map = new HashMap<>();
            map.put("user", resetPasswordDTO);
            map.put("tokenTime", otpService.EXPIRE_MINUTES + token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        return new ResponseEntity<>("Gửi OTP thất bại!", HttpStatus.BAD_REQUEST);


    }

    public void sendSMS(String phone, String token) {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"),
                System.getenv("TWILIO_AUTH_TOKEN"));

        Message.creator(new PhoneNumber(phone.replaceFirst("0","+84")),
                new PhoneNumber("+19844647230"), token).create();
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> processResetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findByPhone(resetPasswordDTO.getPhone()).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + resetPasswordDTO.getPhone()));

        if(otpService.getOtp(resetPasswordDTO.getPhone()) == null){
            return new ResponseEntity<>("Mã OTP đã hết hạn!", HttpStatus.BAD_REQUEST);
        }

        if (resetPasswordDTO.getToken() != otpService.getOtp(resetPasswordDTO.getPhone())) {
            return new ResponseEntity<>("Mã OTP sai!", HttpStatus.BAD_REQUEST);
        }else {

            userService.updatePassword(user, resetPasswordDTO.getPassword());
            return new ResponseEntity<>("Đổi mật khẩu thành công!", HttpStatus.OK);
        }

    }

    @PostMapping("/broker/signup")
    public ResponseEntity<?> createBroker(@RequestHeader(name = "Authorization") String token){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        Role role = roleRepository.findByName("BROKER").get();
        //ko co role broker
        if(!user.getRoles().contains(role)){
            //payment
            UserDTO userDTO = userService.createBroker(user, role);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>("Người dùng đã có tài khoản broker!", HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> getAllBroker(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 8;
        try{
            List<UserDTO> listBroker = userService.getAllBroker(pageNumber, pageSize);
            if(listBroker == null){
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }
            int totalPage = 0;
            if(listBroker.size() % pageSize == 0){
                totalPage = listBroker.size() / pageSize;
            }else{
                totalPage = (listBroker.size() / pageSize) + 1;
            }
            if(totalPage < pageNumber + 1){
                return new ResponseEntity<>("Không tìm thấy kết quả phù hợp!", HttpStatus.BAD_REQUEST);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("listBroker", listBroker);
            map.put("pageNumber", pageNumber+1);
            map.put("totalPage", totalPage);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Không tồn tại trang!", HttpStatus.OK);
        }


    }

    @GetMapping("/user-detail/{userId}")
    public ResponseEntity<?> getDetailUser(@PathVariable(name = "userId") String userId,
                                           @RequestParam(name = "propertyType", required = false) String type,
                                           @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        int id = Integer.parseInt(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 8;
        SearchResponse listPost = postService.getPostByUserId(pageNumber, pageSize, id, type);

        UserDTO userDTO = mapper.map(user, UserDTO.class);
        Map<String, Object> map = new HashMap<>();
        map.put("user", userDTO);
        map.put("listPost", listPost);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<String> updateUser(@RequestHeader(name = "Authorization") String token,
                                            @Valid @RequestBody UserDTO userDTO){
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        String updateStatus = userService.updateUser(phone, userDTO);
        return new ResponseEntity<>(updateStatus, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBroker(@RequestParam(name = "name", defaultValue = "") String fullName,
                                          @RequestParam(name = "ward", defaultValue = "") String ward,
                                          @RequestParam(name = "district", defaultValue = "") String district,
                                          @RequestParam(name = "province", defaultValue = "") String province,
                                          @RequestParam(name = "address", defaultValue = "") String address){


        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}