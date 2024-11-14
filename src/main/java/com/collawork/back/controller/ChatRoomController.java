package com.collawork.back.controller;

import com.collawork.back.model.ChatRooms;
import com.collawork.back.model.User;
import com.collawork.back.repository.ChatRoomRepository;
import com.collawork.back.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/chatrooms")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRooms> getChatRoomInfo(@PathVariable Long chatRoomId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        Optional<ChatRooms> chatRoom = chatRoomRepository.findById(chatRoomId);

        if (chatRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(chatRoom.get());
    }

    @PostMapping("/ProjectChat")
    public ResponseEntity<Object> saveProjectChatRoom(
            @RequestParam("roomName") String roomName, @RequestParam("createdBy") User createdBy,
                                                         HttpServletRequest request) {


        System.out.println("ProjectChat 의 roomName : " + roomName);

        String token = request.getHeader("Authorization");

        System.out.println("token : " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        ChatRooms chatRoom = new ChatRooms();
        chatRoom.setCreatedBy(createdBy); // 생성자 id
        chatRoom.setRoomName(roomName); // 채팅방 이름

        LocalDate localDate = LocalDate.now();
        chatRoom.setCreatedAt(localDate.atStartOfDay()); // 채팅방 생성 시간
        Object result = chatRoomRepository.save(chatRoom);

        if(result != null){
            return ResponseEntity.ok(result);
        }else{
            return ResponseEntity.status(403).body("채팅방 생성 실패.");
        }
    }
}
