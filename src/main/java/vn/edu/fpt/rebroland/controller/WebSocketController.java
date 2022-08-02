package vn.edu.fpt.rebroland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.rebroland.payload.TextMessageDTO;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/socket")
public class WebSocketController {
    @Autowired
    SimpMessagingTemplate template;

    @PostMapping("/send/{userId}")
    public ResponseEntity<Void> sendMessage(@PathVariable(name = "userId") String userId,
                                            @RequestBody TextMessageDTO textMessageDTO) {
        template.convertAndSend("/topic/message/" + userId, textMessageDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload TextMessageDTO textMessageDTO) {
        // receive message from client
    }

    @SendTo("/topic/message")
    public TextMessageDTO broadcastMessage(@Payload TextMessageDTO textMessageDTO) {
        return textMessageDTO;
    }
}