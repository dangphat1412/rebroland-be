package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.NotificationDTO;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.ReportService;
import org.cloudinary.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/notification")
public class NotificationController {

    private NotificationService notificationService;

    private ReportService reportService;

    private UserRepository userRepository;

    public NotificationController(NotificationService notificationService, ReportService reportService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllNotificationByUserId(@RequestHeader(name = "Authorization") String token,
                                                        @RequestParam(name = "pageNo", defaultValue = "0") String pageNo){
        try{
            User user = reportService.getUserByToken(token);
            int pageNumber = Integer.parseInt(pageNo);
            List<NotificationDTO> listNotification = notificationService.getUnreadNotificationPaging(user.getId(), pageNumber);
            user.setUnreadNotification(0);
            userRepository.save(user);
            return new ResponseEntity<>(listNotification, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.OK);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable(name = "id") String id){
        int notificationId = Integer.parseInt(id);
        NotificationDTO notificationDTO = notificationService.getDetailNotificationById(notificationId);
        return new ResponseEntity<>(notificationDTO, HttpStatus.OK);
    }

}
