package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ContactService;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.UserCareService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/contact")
public class ContactController {
    private ContactService contactService;

    private PostRepository postRepository;

    private UserRepository userRepository;

    private UserCareService userCareService;

    private NotificationService notificationService;

    public ContactController(ContactService contactService, PostRepository postRepository,
                             UserRepository userRepository, UserCareService userCareService,
                             NotificationService notificationService) {
        this.contactService = contactService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userCareService = userCareService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<?> getContactsByUserId(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                 @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);
        int pageSize = 5;
        int pageNumber = Integer.parseInt(pageNo);
        ContactResponse contactList = contactService.getContactByUserId(userId, keyword, pageNumber, pageSize);
//        List<CareDTO> userCareDTOList = userCareService.getByUserId(userId);
//        Map<String, Object> map = new HashMap<>();
//        map.put("contactList", contactList);
//        map.put("caringList",userCareDTOList);
        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @Autowired
    SimpMessagingTemplate template;

    @PostMapping("/{userId}/{postId}")
    @Transactional
    public ResponseEntity<String> createContact(@PathVariable int userId,
                                                @PathVariable int postId,
                                                @Valid @RequestBody ContactDTO contactDTO,
                                                @RequestHeader(name = "Authorization") String token) {

        User userRequest = getUserFromToken(token);
        if(userRequest.getId() == userId){
            return new ResponseEntity<>("Không thể gửi liên lạc!", HttpStatus.BAD_REQUEST);
        }
        ContactDTO newContact = contactService.createContact(contactDTO, userId, postId, userRequest.getId());
        TextMessageDTO messageDTO = new TextMessageDTO();
        String message = "Tin nhắn từ SĐT " + userRequest.getPhone() + ": " + contactDTO.getContent();
        messageDTO.setMessage(message);
        template.convertAndSend("/topic/message/" + userId, messageDTO);

        //save notification table
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(userId);
        notificationDTO.setContent(contactDTO.getContent());
        notificationDTO.setPhone(userRequest.getPhone());
        notificationDTO.setType("Contact");
        notificationService.createContactNotification(notificationDTO);

        //update unread notification user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        int numberUnread = user.getUnreadNotification();
        numberUnread++;
        user.setUnreadNotification(numberUnread);
        userRepository.save(user);

        //send message to user or broker
//        sendSMS(user.getPhone(), message);
        return new ResponseEntity<>("Create Contact Successfully !!!", HttpStatus.CREATED);

    }


    public void sendSMS(String phone, String token) {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"),
                System.getenv("TWILIO_AUTH_TOKEN"));

        Message.creator(new PhoneNumber(phone.replaceFirst("0","+84")),
                new PhoneNumber("+19844647230"), token).create();
    }

    @DeleteMapping("/{contactId}")
    @Transactional
    public ResponseEntity<String> deleteContact(@PathVariable int contactId,
                                                @RequestHeader(name = "Authorization") String token) {
        ContactDTO contactDTO = contactService.getContactById(contactId);
        int userId = getUserIdFromToken(token);
        if (userId != contactDTO.getUser().getId()) {
            return new ResponseEntity<>("You don't have permission to delete !!!", HttpStatus.BAD_REQUEST);
        } else {
            contactService.deleteContact(contactId);
            return new ResponseEntity<>("Delete Contact Successfully !!!", HttpStatus.NO_CONTENT);
        }
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

    private User getUserFromToken(String token) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        return user;
    }
}
