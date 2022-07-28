package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ReportDTO;
import vn.edu.fpt.rebroland.payload.UserRateDTO;
import vn.edu.fpt.rebroland.service.PostService;
import vn.edu.fpt.rebroland.service.ReportService;
import vn.edu.fpt.rebroland.service.UserRateService;
import org.cloudinary.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/report")
public class ReportController {

    private ReportService reportService;

    private UserRateService userRateService;

    public ReportController(ReportService reportService, UserRateService userRateService) {
        this.reportService = reportService;
        this.userRateService = userRateService;
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> createReportPost(@RequestHeader(name = "Authorization") String token,
                                            @Valid @RequestBody ReportDTO reportPostDTO,
                                              @PathVariable(name = "postId") String id){
        User user = reportService.getUserByToken(token);
        reportPostDTO.setUserId(user.getId());
        reportPostDTO.setRoleId(user.getCurrentRole());

        int postId = Integer.parseInt(id);
        reportPostDTO.setPostId(postId);

        ReportDTO dto = reportService.createReport(reportPostDTO);
        if(dto == null){
            return new ResponseEntity<>("Báo cáo thất bại!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createReportUser(@RequestHeader(name = "Authorization") String token,
                                              @Valid @RequestBody ReportDTO reportDTO,
                                              @PathVariable(name = "userId") String id){
        User user = reportService.getUserByToken(token);
        reportDTO.setUserId(user.getId());
        reportDTO.setRoleId(user.getCurrentRole());

        int userReportedId = Integer.parseInt(id);
//        User userReported = reportService.getUserById(userReportedId);
        reportDTO.setUserReportedId(userReportedId);

        ReportDTO dto = reportService.createReport(reportDTO);
        if(dto == null){
            return new ResponseEntity<>("Báo cáo thất bại!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

//    @PostMapping("/broker/{userId}")
//    public ResponseEntity<?> createReportBroker(@RequestHeader(name = "Authorization") String token,
//                                              @Valid @RequestBody ReportDTO reportDTO,
//                                              @PathVariable(name = "userId") String id){
//        User user = reportService.getUserByToken(token);
//        reportDTO.setUserId(user.getId());
//        reportDTO.setRoleId(user.getCurrentRole());
//
//        int userReportedId = Integer.parseInt(id);
//        User userReported = reportService.getUserById(userReportedId);
//        if(userReported.getRoles().size() != 2){
//            return new ResponseEntity<>("Người bị báo cáo không phải là broker!", HttpStatus.BAD_REQUEST);
//        }
//        reportDTO.setUserReportedId(userReportedId);
//
//        reportDTO.setRoleReportedId(3);
//
//        ReportDTO dto = reportService.createReport(reportDTO);
//        if(dto == null){
//            return new ResponseEntity<>("Báo cáo thất bại!", HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.CREATED);
//    }

    @PostMapping("/rating/{userId}")
    public ResponseEntity<?> rateUser(@RequestHeader(name = "Authorization") String token,
                                      @Valid @RequestBody UserRateDTO userRateDTO,
                                      @PathVariable(name = "userId") String id){
        User user = reportService.getUserByToken(token);
        userRateDTO.setUserId(user.getId());
        userRateDTO.setRoleId(user.getCurrentRole());

        int userRatedId = Integer.parseInt(id);
        userRateDTO.setUserRated(userRatedId);
        userRateDTO.setUserRoleRated(2);

        UserRateDTO dto = userRateService.createUserRate(userRateDTO);
        if(dto == null){
            return new ResponseEntity<>("Đánh giá thất bại!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/rating/broker/{userId}")
    public ResponseEntity<?> rateBroker(@RequestHeader(name = "Authorization") String token,
                                      @Valid @RequestBody UserRateDTO userRateDTO,
                                      @PathVariable(name = "userId") String id){
        User user = reportService.getUserByToken(token);
        userRateDTO.setUserId(user.getId());
        userRateDTO.setRoleId(user.getCurrentRole());

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
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/starRate/user/{userId}")
    public ResponseEntity<?> getStarRateOfUserRated(@PathVariable(name = "userId") String userId){
        try {
            int userRatedId = Integer.parseInt(userId);
            float starRate = userRateService.getStarRateOfUserRated(userRatedId, 2);
            return new ResponseEntity<>(starRate, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/starRate/broker/{userId}")
    public ResponseEntity<?> getStarRateOfBrokerRated(@PathVariable(name = "userId") String userId){
        try {
            int userRatedId = Integer.parseInt(userId);
            float starRate = userRateService.getStarRateOfUserRated(userRatedId, 3);
            return new ResponseEntity<>(starRate, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



}
