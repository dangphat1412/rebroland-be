package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.Role;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.*;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/admin")
public class AdminController {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserService userService;
    private PostService postService;
    private ReportService reportService;
    private NotificationService notificationService;
    private PaymentService paymentService;
    private WithdrawService withdrawService;
    private PriceService priceService;

    public AdminController(UserRepository userRepository, UserService userService, PostService postService, RoleRepository roleRepository,
                           ReportService reportService, NotificationService notificationService, PaymentService paymentService,
                           WithdrawService withdrawService, PriceService priceService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.postService = postService;
        this.roleRepository = roleRepository;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.withdrawService = withdrawService;
        this.priceService = priceService;
    }

    @GetMapping("/list-users")
    public ResponseEntity<?> getAllAccounts(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                        @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                        @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        User user = getUserFromToken(token);
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        Role role = roleRepository.findByName("ADMIN").get();
        if(user.getRoles().contains(role)){
            List<UserDTO> listUser = userService.getAllUserForAdminPaging(user.getId(), pageNumber, pageSize, keyword, sortValue);

            List<UserDTO> listAllUser = userService.getAllUserForAdmin(user.getId(), keyword, sortValue);
            int totalPage = 0;
            if(listAllUser.size() % pageSize == 0){
                totalPage = listAllUser.size() / pageSize;
            }else{
                totalPage = (listAllUser.size() / pageSize) + 1;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("list", listUser);
            map.put("pageNo", pageNumber+1);
            map.put("totalPages", totalPage);
            map.put("totalResult", listAllUser.size());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-users/{userId}")
    public ResponseEntity<?> getPostsOfUser(@RequestHeader(name = "Authorization") String token,
                                              @PathVariable(name = "userId") int userId,
                                              @RequestParam(name = "pageNo", defaultValue = "0") String pageNo
                                              ){
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        SearchResponse listPost = postService.getAllPostByUserId(pageNumber, pageSize, userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    @PutMapping("/user/status/{userId}")
    public ResponseEntity<?> changeLockStatusOfAccount(@PathVariable(name = "userId") int userId){
        boolean status = userService.changeStatusOfUser(userId);
        if(status){
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-posts")
    public ResponseEntity<?> getAllPosts(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                        @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                        @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            SearchResponse listUser = postService.getAllPost(pageNumber, pageSize, keyword, sortValue);
            return new ResponseEntity<>(listUser, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/list-posts/{postId}")
    public ResponseEntity<?> getDetailPost(@RequestHeader(name = "Authorization") String token,
                                        @PathVariable(name = "postId") int postId){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            PostDTO dto = postService.getPostByPostId(postId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    SimpMessagingTemplate template;

    @PutMapping("/post/status/{postId}")
    public ResponseEntity<?> changeLockStatusOfPost(@PathVariable(name = "postId") int postId){
        int status = postService.changeStatusOfPost(postId);
//        PostDTO post = postService.getPostByPostId(postId);
//        if(post == null){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        if(status == 1){
            return new ResponseEntity<>("Thay đổi trạng thái thành công!", HttpStatus.OK);
        }
        if(status == 0){
            return new ResponseEntity<>("Người đăng bị block!",HttpStatus.BAD_REQUEST);
        }
        if(status == 2){
            return new ResponseEntity<>("Bài viết đã bị xóa!",HttpStatus.BAD_REQUEST);
        }
        if(status == 3){
            return new ResponseEntity<>("Bài viết không tồn tại",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Đã xảy ra lỗi!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/list-reports/posts")
    public ResponseEntity<?> getReportedPosts(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                          @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                          @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            SearchResponse reportResponse = reportService.getListReportPost(pageNumber, pageSize, keyword, sortValue);
            return new ResponseEntity<>(reportResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-reports/posts/{reportId}")
    public ResponseEntity<?> getPostReporters(@RequestHeader(name = "Authorization") String token,
                                              @PathVariable(name = "reportId") int reportId,
                                              @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            ReportDetailResponse reportResponse = reportService.getListDetailReport(reportId, pageNumber, pageSize);
            return new ResponseEntity<>(reportResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-reports/users")
    public ResponseEntity<?> getReportedUsers(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                              @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                              @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            ReportResponse listReportPost = reportService.getListReportUser(pageNumber, pageSize, keyword, sortValue);
            return new ResponseEntity<>(listReportPost, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-reports")
    public ResponseEntity<?> getListReport(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                              @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                              @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            ReportResponse listReportUser = reportService.getListReportUser(pageNumber, pageSize, keyword, sortValue);
            SearchResponse reportResponse = reportService.getListReportPost(pageNumber, pageSize, keyword, sortValue);
            Map<String, Object> map = new HashMap<>();
            map.put("listPost", reportResponse);
            map.put("listUser", listReportUser);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-reports/users/{reportId}")
    public ResponseEntity<?> getUserReporters(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                              @PathVariable(name = "reportId") int reportId){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            ReportDetailResponse reportResponse = reportService.getListDetailReport(reportId, pageNumber, pageSize);
            return new ResponseEntity<>(reportResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/list-reports/posts/accept/{reportId}")
    public ResponseEntity<?> acceptPostReport(@PathVariable(name = "reportId") int reportId,
                                              @RequestHeader(name = "Authorization") String token,
                                              @Valid @RequestBody ReportDTO reportDTO){

        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        if(user.getRoles().contains(role)){
            boolean result = reportService.acceptReportPost(reportId, reportDTO.getComment());


            if(result){
                return new ResponseEntity<>("Đã giải quyết báo cáo !", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }

        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/list-reports/posts/reject/{reportId}")
    public ResponseEntity<?> rejectPostReport(@PathVariable(name = "reportId") int reportId,
                                              @RequestHeader(name = "Authorization") String token){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        if(user.getRoles().contains(role)){
            boolean result = reportService.rejectReportPost(reportId);
            if(result){
                return new ResponseEntity<>("Đã hủy bỏ báo cáo !", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/list-reports/users/accept/{reportId}")
    public ResponseEntity<?> acceptUserReport(@PathVariable(name = "reportId") int reportId,
                                              @RequestHeader(name = "Authorization") String token,
                                              @Valid @RequestBody ReportDTO reportDTO){

        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        if(user.getRoles().contains(role)){
            boolean result = reportService.acceptReportUser(reportId, reportDTO.getComment());
            if(result){
                return new ResponseEntity<>("Đã giải quyết báo cáo !", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }

        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/list-reports/users/reject/{reportId}")
    public ResponseEntity<?> rejectUserReport(@PathVariable(name = "reportId") int reportId,
                                              @RequestHeader(name = "Authorization") String token){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        if(user.getRoles().contains(role)){
            boolean result = reportService.rejectReportUser(reportId);
            if(result){
                return new ResponseEntity<>("Đã hủy bỏ báo cáo !", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-payments")
    public ResponseEntity<?> getAllPayments(@RequestHeader(name = "Authorization") String token,
                                            @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                            @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                            @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 10;
        if(user.getRoles().contains(role)){
            PaymentResponse listPayment = paymentService.getAllPayments(pageNumber, pageSize, keyword, sortValue);
            return new ResponseEntity<>(listPayment, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-payments/total")
    public ResponseEntity<?> getTotalRevenue(@RequestHeader(name = "Authorization") String token){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            Map<String, Long> map = paymentService.getTotalMoney();
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-cashout/direct-withdraw")
    public ResponseEntity<?> getListCashOutDirectWithdraw(@RequestHeader(name = "Authorization") String token,
                                                          @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                          @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                                          @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            WithdrawResponse list = withdrawService.getAllDirectWithdraw(pageNumber, pageSize, keyword, sortValue);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/list-cashout/accept/{withdrawId}")
    public ResponseEntity<?> acceptWithdraw(@RequestHeader(name = "Authorization") String token,
                                                  @PathVariable(name = "withdrawId") int withdrawId){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            boolean result = withdrawService.acceptWithdraw(withdrawId);
            if(result){
                return new ResponseEntity<>("Đã giải quyết yêu cầu rút tiền !", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/list-cashout/reject/{withdrawId}")
    public ResponseEntity<?> rejectWithdraw(@RequestHeader(name = "Authorization") String token,
                                                  @PathVariable(name = "withdrawId") int withdrawId,
                                                  @Valid @RequestBody WithdrawDTO withdrawDTO){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            boolean result = withdrawService.rejectWithdraw(withdrawId, withdrawDTO.getContent());
            if(result){
                return new ResponseEntity<>("Đã từ chối yêu cầu rút tiền !", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-cashout/transfer-withdraw")
    public ResponseEntity<?> getListCashOutTransferWithdraw(@RequestHeader(name = "Authorization") String token,
                                                            @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                            @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                                            @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            WithdrawResponse list = withdrawService.getAllTransferWithdraw(pageNumber, pageSize, keyword, sortValue);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list-cashout")
    public ResponseEntity<?> getListCashOut(@RequestHeader(name = "Authorization") String token,
                                                          @RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                          @RequestParam(name = "sortValue", defaultValue = "0") String sortValue,
                                                          @RequestParam(name = "keyword", defaultValue = "") String keyword){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        if(user.getRoles().contains(role)){
            WithdrawResponse listDirect = withdrawService.getAllDirectWithdraw(pageNumber, pageSize, keyword, sortValue);
            WithdrawResponse listTransfer = withdrawService.getAllTransferWithdraw(pageNumber, pageSize, keyword, sortValue);
            Map<String, Object> map = new HashMap<>();
            map.put("direct", listDirect);
            map.put("transfer", listTransfer);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-price")
    public ResponseEntity<?> updatePrice(@RequestHeader(name = "Authorization") String token,
                                      @Valid @RequestBody ListPrice listPrice){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            priceService.createPrice(listPrice);
//            if(dto == null){
//                return new ResponseEntity<>("Giá đã tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
//            }
            return new ResponseEntity<>("Cập nhật giá thành công!", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/list-price/price-post")
    public ResponseEntity<?> updatePricePost(@RequestHeader(name = "Authorization") String token,
                                      @Valid @RequestBody PriceDTO priceDTO){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            PriceDTO dto = priceService.createPostPrice(priceDTO);
            if(dto == null){
                return new ResponseEntity<>("Giá đã tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/list-price/price-post")
    public ResponseEntity<?> getListPostPrice(@RequestHeader(name = "Authorization") String token){
        User user = getUserFromToken(token);
        Role role = roleRepository.findByName("ADMIN").get();

        if(user.getRoles().contains(role)){
            Map<String, Object> map = priceService.getListPostPrice();
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
        }
    }

//    @PutMapping("/active-price/{priceId}")
//    public ResponseEntity<?> activePrice(@RequestHeader(name = "Authorization") String token,
//                                         @PathVariable(name = "priceId") int priceId){
//        User user = getUserFromToken(token);
//        Role role = roleRepository.findByName("ADMIN").get();
//
//        if(user.getRoles().contains(role)){
//            PriceDTO priceDTO = priceService.getPriceBroker(priceId);
//            PriceDTO dto = priceService.getPriceByTypeIdAndUnitDate(priceDTO.getTypeId(), priceDTO.getUnitDate());
//
//            priceDTO.setStatus(true);
//            priceService.createPrice(priceDTO);
//
//            dto.setStatus(false);
//            priceService.createPrice(dto);
//            if(dto == null){
//                return new ResponseEntity<>("Giá đã tồn tại trong hệ thống!", HttpStatus.BAD_REQUEST);
//            }
//            return new ResponseEntity<>(dto, HttpStatus.OK);
//        }else{
//            return new ResponseEntity<>("Người dùng không phải admin!", HttpStatus.BAD_REQUEST);
//        }
//    }



    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }


    private User getUserFromToken(String token) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        return user;
    }
}
