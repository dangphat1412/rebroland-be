package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.ContactRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ContactService;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.UserCareService;
import com.pusher.rest.Pusher;
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
import java.util.Collections;

@RestController
@CrossOrigin(origins = "https://rebroland.vercel.app")
@RequestMapping("/api/contact")
public class ContactController {
    private ContactService contactService;

    private PostRepository postRepository;

    private UserRepository userRepository;

    private UserCareService userCareService;

    private NotificationService notificationService;

    private ContactRepository contactRepository;

    public ContactController(ContactService contactService, PostRepository postRepository,
                             UserRepository userRepository, UserCareService userCareService,
                             NotificationService notificationService, ContactRepository contactRepository) {
        this.contactService = contactService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userCareService = userCareService;
        this.notificationService = notificationService;
        this.contactRepository = contactRepository;
    }

    @GetMapping("/broker")
    public ResponseEntity<?> getContactsByBrokerId(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                   @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                   @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);
        int pageSize = 5;
        int pageNumber = Integer.parseInt(pageNo);
        ContactResponse contactList = contactService.getContactByBrokerId(userId, keyword, pageNumber, pageSize);
//        List<CareDTO> userCareDTOList = userCareService.getByUserId(userId);
//        Map<String, Object> map = new HashMap<>();
//        map.put("contactList", contactList);
//        map.put("caringList",userCareDTOList);
        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getContactsByUserId(@RequestParam(name = "pageNo", defaultValue = "0") String pageNo,
                                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                 @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);
        int pageSize = 5;
        int pageNumber = Integer.parseInt(pageNo);
        ContactResponse contactList = contactService.getContactByUserId(userId, keyword, pageNumber, pageSize);
        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @PostMapping("/user/confirm/{contactId}")
    public ResponseEntity<?> confirmContact(@RequestHeader(name = "Authorization") String token,
                                            @PathVariable(name = "contactId") int contactId) {
        User user = getUserFromToken(token);
        if (user.getRoles().size() == 1) {
            contactService.deleteContact(contactId);
            return new ResponseEntity<>("Đã xác nhận yêu cầu !", HttpStatus.OK);
        }
        if (user.getRoles().size() == 2) {
            ContactDTO contactDTO = contactService.getContactById(contactId);
            contactDTO.setRoleId(3);
            contactService.createBrokerContact(contactDTO);
            return new ResponseEntity<>("Đã chuyển yêu cầu sang cho broker!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Đã xảy ra lỗi !", HttpStatus.BAD_REQUEST);
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
        if (userRequest.getId() == userId) {
            return new ResponseEntity<>("Không thể gửi liên lạc!", HttpStatus.BAD_REQUEST);
        }
        if (postId == 0) {
            ContactDTO dto2 = contactService.getContactByUserIdAndPostIdNull(userRequest.getId(), userId, contactDTO.getRoleId());
            if (dto2 != null) {
                return new ResponseEntity<>("Bạn đã gửi liên hệ rồi! Hãy đợi liên hệ lại nhé!", HttpStatus.BAD_REQUEST);
            }
        }
        ContactDTO dto = contactService.getContactByUserIdAndPostId(userRequest.getId(), userId, postId, contactDTO.getRoleId());
        if (dto != null) {
            return new ResponseEntity<>("Bạn đã gửi liên hệ rồi! Hãy đợi nhà môi giới liên hệ lại nhé!", HttpStatus.BAD_REQUEST);
        }

        ContactDTO newContact = contactService.createContact(contactDTO, userId, postId, userRequest.getId());
//        TextMessageDTO messageDTO = new TextMessageDTO();
        String message = "Tin nhắn từ SĐT " + userRequest.getPhone() + ": " + contactDTO.getContent();
//        messageDTO.setMessage(message);
//        template.convertAndSend("/topic/message/" + userId, messageDTO);
        Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
        pusher.setCluster("ap1");
        pusher.setEncrypted(true);
        pusher.trigger("my-channel-" + userId, "my-event", Collections.singletonMap("message", message));

        //save notification table
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(userId);
        if (contactDTO.getContent() == null) {
            notificationDTO.setContent("");
        } else {
            notificationDTO.setContent(contactDTO.getContent());
        }
        notificationDTO.setPhone(userRequest.getPhone());
        notificationDTO.setType("Contact");
        notificationDTO.setPostId(postId);
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

        Message.creator(new PhoneNumber(phone.replaceFirst("0", "+84")),
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
