package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.AvgRate;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.UserRateDTO;
import vn.edu.fpt.rebroland.repository.AvgRateRepository;
import vn.edu.fpt.rebroland.service.ReportService;
import vn.edu.fpt.rebroland.service.UserRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/rating")
public class UserRateController {
    private UserRateService userRateService;
    private AvgRateRepository rateRepository;
    private ReportService reportService;

    public UserRateController(UserRateService userRateService, AvgRateRepository rateRepository, ReportService reportService) {
        this.userRateService = userRateService;
        this.rateRepository = rateRepository;
        this.reportService = reportService;
    }

    @PostMapping("/{userId}")
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
        return new ResponseEntity<>(dto, HttpStatus.OK);
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
}
