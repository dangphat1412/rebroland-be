package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.AvgRate;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ListUserRate;
import vn.edu.fpt.rebroland.payload.UserRateDTO;
import vn.edu.fpt.rebroland.payload.UserRateResponse;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ReportService;
import vn.edu.fpt.rebroland.service.UserRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/rating")
public class UserRateController {
    private UserRateService userRateService;
    private AvgRateRepository rateRepository;
    private ReportService reportService;
    private UserRepository userRepository;

    public UserRateController(UserRateService userRateService, AvgRateRepository rateRepository, ReportService reportService,
                              UserRepository userRepository) {
        this.userRateService = userRateService;
        this.rateRepository = rateRepository;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> rateUser(@RequestHeader(name = "Authorization") String token,
                                      @Valid @RequestBody UserRateDTO userRateDTO,
                                      @PathVariable(name = "userId") String id){
        User user = reportService.getUserByToken(token);
        userRateDTO.setUserId(user.getId());

        int userRatedId = Integer.parseInt(id);
        userRateDTO.setUserRated(userRatedId);
        userRateDTO.setUserRoleRated(2);

        UserRateDTO dto = userRateService.createUserRate(userRateDTO);
        if(dto == null){
            return new ResponseEntity<>("Đánh giá thất bại!", HttpStatus.BAD_REQUEST);
        }

        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userRatedId, 2);
        Map<String, Object> map = new HashMap<>();
        if (avgRate != null) {
            map.put("starRate", avgRate.getAvgRate());
        } else {
            map.put("starRate", 0);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //rate broker
    @PostMapping("/broker/{userId}")
    public ResponseEntity<?> rateBroker(@RequestHeader(name = "Authorization") String token,
                                        @Valid @RequestBody UserRateDTO userRateDTO,
                                        @PathVariable(name = "userId") String id){
        User user = reportService.getUserByToken(token);
        userRateDTO.setUserId(user.getId());

        int userRatedId = Integer.parseInt(id);
        User userRated = reportService.getUserById(userRatedId);
        if(userRated.getRoles().size() != 2){
            return new ResponseEntity<>("Người được đánh giá không phải là broker!", HttpStatus.BAD_REQUEST);
        }
        userRateDTO.setUserRated(userRatedId);
        userRateDTO.setUserRoleRated(3);

        UserRateDTO dto = userRateService.createUserRate(userRateDTO);
        if(dto == null){
            return new ResponseEntity<>("Đánh giá thất bại!", HttpStatus.BAD_REQUEST);
        }

        AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userRatedId, 3);
        Map<String, Object> map = new HashMap<>();
        if (avgRate != null) {
            map.put("starRate", avgRate.getAvgRate());
        } else {
            map.put("starRate", 0);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/broker/list")
    public ResponseEntity<?> rateListBroker(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody ListUserRate listDto){
        if(listDto == null){
            return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
        }
        for (UserRateDTO userRateDTO: listDto.getLists()) {
            if(userRateDTO.getStarRate() == 0){
                break;
            }
            User user = reportService.getUserByToken(token);
            userRateDTO.setUserId(user.getId());

            int userRatedId = userRateDTO.getUserRated();
//            User userRated = userRepository.getUserById(userRatedId);
//            if(userRated.getRoles().size() != 2){
//                return new ResponseEntity<>("Người được đánh giá không phải là broker!", HttpStatus.BAD_REQUEST);
//            }
            userRateDTO.setUserRated(userRatedId);
            userRateDTO.setUserRoleRated(3);

            UserRateDTO dto = userRateService.createUserRate(userRateDTO);
            if(dto == null){
                return new ResponseEntity<>("Đánh giá thất bại!", HttpStatus.BAD_REQUEST);
            }

//            AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userRatedId, 3);
//            Map<String, Object> map = new HashMap<>();
//            if (avgRate != null) {
//                map.put("starRate", avgRate.getAvgRate());
//            } else {
//                map.put("starRate", 0);
//            }

        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getListUserRateOfUser(@PathVariable(name = "userId") int userId,
                                                     @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        User user = userRepository.getUserById(userId);
        if(user != null){
            UserRateResponse list = userRateService.getListUserRate(userId, 2, pageNumber, pageSize);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Người dùng không tồn tại trong hệ thống!" ,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/broker/{userId}")
    public ResponseEntity<?> getListUserRateOfBroker(@PathVariable(name = "userId") int userId,
                                                     @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        int pageNumber = Integer.parseInt(pageNo);
        int pageSize = 5;
        User user = userRepository.getUserById(userId);
        if(user != null){
            UserRateResponse list = userRateService.getListUserRate(userId, 3, pageNumber, pageSize);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Người dùng không tồn tại trong hệ thống!" ,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile/check/{userRatedId}/{roleId}")
    public ResponseEntity<?> checkRateInProfile(@PathVariable(name = "userRatedId") int userRatedId,
                                                @PathVariable(name = "roleId") int roleId,
                                                @RequestHeader(name = "Authorization") String token){
        User user = reportService.getUserByToken(token);
        if(user != null) {
            UserRateDTO rateDTO = userRateService.getUserRateStartDateMax(user.getId(), userRatedId, roleId);
            if (rateDTO != null) {
                Calendar cal = Calendar.getInstance();
                Date date = rateDTO.getStartDate();
                cal.setTime(date);
                cal.add(Calendar.SECOND, 30);
                Date newDate = cal.getTime();
                long millis = System.currentTimeMillis();
                java.sql.Date now = new java.sql.Date(millis);
                if (now.compareTo(newDate) < 0) {
                    return new ResponseEntity<>("Không thể đánh giá !", HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }else {
            return new ResponseEntity<>("Người dùng không tồn tại trong hệ thống!" ,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/profile/user/{userId}")
    public ResponseEntity<?> rateUserInProfile(@Valid @RequestBody UserRateDTO userRateDTO,
                                               @PathVariable(name = "userId") String id,
                                                @RequestHeader(name = "Authorization") String token){
        User user = reportService.getUserByToken(token);
        if(user != null){
            int userRatedId = Integer.parseInt(id);
            UserRateDTO rateDTO = userRateService.getUserRateStartDateMax(user.getId(), userRatedId, 2);
            if (rateDTO != null) {
                Calendar cal = Calendar.getInstance();
                Date date = rateDTO.getStartDate();
                cal.setTime(date);
                cal.add(Calendar.SECOND, 30);
                Date newDate = cal.getTime();
                long millis = System.currentTimeMillis();
                java.sql.Date now = new java.sql.Date(millis);
                if (now.compareTo(newDate) < 0) {
                    return new ResponseEntity<>("Không thể đánh giá !", HttpStatus.BAD_REQUEST);
                }
            }

            userRateDTO.setUserId(user.getId());

            userRateDTO.setUserRated(userRatedId);
            userRateDTO.setUserRoleRated(2);
            UserRateDTO dto = userRateService.createUserRate(userRateDTO);
            if(dto == null){
                return new ResponseEntity<>("Đánh giá thất bại!", HttpStatus.BAD_REQUEST);
            }

            AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userRatedId, 2);
            Map<String, Object> map = new HashMap<>();
            if (avgRate != null) {
                map.put("starRate", avgRate.getAvgRate());
            } else {
                map.put("starRate", 0);
            }
            return new ResponseEntity<>(map, HttpStatus.OK);
//            return new ResponseEntity<>(dto, HttpStatus.OK);

        }else {
            return new ResponseEntity<>("Người dùng không tồn tại trong hệ thống!" ,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/profile/broker/{userId}")
    public ResponseEntity<?> rateBrokerInProfile(@Valid @RequestBody UserRateDTO userRateDTO,
                                               @PathVariable(name = "userId") String id,
                                               @RequestHeader(name = "Authorization") String token){
        User user = reportService.getUserByToken(token);
        if(user != null){
            int userRatedId = Integer.parseInt(id);
            UserRateDTO rateDTO = userRateService.getUserRateStartDateMax(user.getId(), userRatedId, 3);
            if (rateDTO != null) {
                Calendar cal = Calendar.getInstance();
                Date date = rateDTO.getStartDate();
                cal.setTime(date);
                cal.add(Calendar.SECOND, 30);
                Date newDate = cal.getTime();
                long millis = System.currentTimeMillis();
                java.sql.Date now = new java.sql.Date(millis);
                if (now.compareTo(newDate) < 0) {
                    return new ResponseEntity<>("Không thể đánh giá!", HttpStatus.BAD_REQUEST);
                }
            }

            userRateDTO.setUserId(user.getId());
            User userRated = reportService.getUserById(userRatedId);
            if(userRated.getRoles().size() != 2){
                return new ResponseEntity<>("Người được đánh giá không phải là broker!", HttpStatus.BAD_REQUEST);
            }
            userRateDTO.setUserRated(userRatedId);
            userRateDTO.setUserRoleRated(3);

            UserRateDTO dto = userRateService.createUserRate(userRateDTO);
            if(dto == null){
                return new ResponseEntity<>("Đánh giá thất bại!", HttpStatus.BAD_REQUEST);
            }else{
                AvgRate avgRate = rateRepository.getAvgRateByUserIdAndRoleId(userRatedId, 3);
                Map<String, Object> map = new HashMap<>();
                if (avgRate != null) {
                    map.put("starRate", avgRate.getAvgRate());
                } else {
                    map.put("starRate", 0);
                }
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }else {
            return new ResponseEntity<>("Người dùng không tồn tại trong hệ thống!" ,HttpStatus.BAD_REQUEST);
        }
    }
}
