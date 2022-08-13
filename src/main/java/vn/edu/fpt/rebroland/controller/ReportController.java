package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ReportDTO;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.service.ReportService;
import vn.edu.fpt.rebroland.service.UserRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/report")
public class ReportController {

    private ReportService reportService;

    private UserRateService userRateService;

    private AvgRateRepository rateRepository;

    public ReportController(ReportService reportService, UserRateService userRateService, AvgRateRepository avgRateRepository) {
        this.reportService = reportService;
        this.userRateService = userRateService;
        this.rateRepository = avgRateRepository;
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> createReportPost(@RequestHeader(name = "Authorization") String token,
                                            @Valid @RequestBody ReportDTO reportPostDTO,
                                              @PathVariable(name = "postId") String id){
        User user = reportService.getUserByToken(token);

        int postId = Integer.parseInt(id);
        reportPostDTO.setPostId(postId);
        reportPostDTO.setUserReportId(user.getId());

        HttpStatus status = reportService.createReport(reportPostDTO);
        return new ResponseEntity<>(status);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createReportUser(@RequestHeader(name = "Authorization") String token,
                                              @Valid @RequestBody ReportDTO reportDTO,
                                              @PathVariable(name = "userId") String id){
        User user = reportService.getUserByToken(token);

        int userReportedId = Integer.parseInt(id);
//        User userReported = reportService.getUserById(userReportedId);
        reportDTO.setUserReportedId(userReportedId);
        reportDTO.setUserReportId(user.getId());

        HttpStatus status = reportService.createReport(reportDTO);
        return new ResponseEntity<>(status);
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
