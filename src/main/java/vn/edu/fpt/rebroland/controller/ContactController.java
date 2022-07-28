package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ContactService;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.UserCareService;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                                 @RequestHeader(name = "Authorization") String token) {

        int userId = getUserIdFromToken(token);
        int pageSize = 5;
        int pageNumber = Integer.parseInt(pageNo);
        ContactResponse contactList = contactService.getContactByUserId(userId, pageNumber, pageSize);
        List<CareDTO> userCareDTOList = userCareService.getByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("contactList", contactList);
        map.put("caringList",userCareDTOList);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Autowired
    SimpMessagingTemplate template;

    @PostMapping("/{userId}/{postId}")
    @Transactional
    public ResponseEntity<String> createContact(@PathVariable int userId,
                                                @PathVariable int postId,
                                                @Valid @RequestBody ContactDTO contactDTO) {

        ContactDTO newContact = contactService.createContact(contactDTO, userId, postId);
        TextMessageDTO messageDTO = new TextMessageDTO();
        String message = "Tin nhắn từ SĐT " + contactDTO.getPhone() + ": " + contactDTO.getContent();
        messageDTO.setMessage(message);
        template.convertAndSend("/topic/message/" + userId, messageDTO);
        //save notification table
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(userId);
        notificationDTO.setContent(contactDTO.getContent());
        notificationDTO.setPhone(contactDTO.getPhone());
        notificationService.createContactNotification(notificationDTO);

        //update unread notification user
//        List<NotificationDTO> listDto = notificationService.getUnreadNotification(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        int numberUnread = user.getUnreadNotification();
        numberUnread++;
        user.setUnreadNotification(numberUnread);
        userRepository.save(user);

        return new ResponseEntity<>("Create Contact Successfully !!!", HttpStatus.CREATED);

    }

//    @SendTo("/topic/message")
//    public ContactDTO broadcastMessage(@Payload ContactDTO textMessageDTO) {
//        return textMessageDTO;
//    }

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
}
