package com.collawork.back.controller;

import com.collawork.back.model.ChatRoomParticipants;
import com.collawork.back.model.ChatRooms;
import com.collawork.back.model.auth.User;
import com.collawork.back.repository.ChatRoomParticipantsRepository;
import com.collawork.back.repository.ChatRoomRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/chatrooms")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomParticipantsRepository chatRoomParticipantsRepository;


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

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(403).body("채팅방 생성 실패.");
        }
    }

    @PostMapping("/new")
    public ResponseEntity<Object> saveNewChatRoom(@RequestBody Map<String, Object> requestData) {
        String roomName = (String) requestData.get("roomName");
        Long createdById = ((Number) requestData.get("created_by")).longValue();
        Long receiverId = ((Number) requestData.get("receiverId")).longValue();

        System.out.println("채팅방 이름: " + roomName);
        System.out.println("생성자 ID: " + createdById);
        System.out.println("초대받은 사람 ID: " + receiverId);

        // 로그인한 사람 id
        Optional<User> find = userRepository.findById(createdById);

        if (find.isEmpty()) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        User createdBy = find.get();

        //메세지 받을 사람 id
        Optional<User> findUser = userRepository.findById(receiverId);
        if (findUser.isEmpty()) {
            return ResponseEntity.status(404).body("초대받은 사용자를 찾을 수 없습니다.");
        }
        User receiver = findUser.get();

        // 기존 채팅방 존재 여부 확인
        List<ChatRoomParticipants> creatorRooms = chatRoomParticipantsRepository.findByUser(createdBy);
        List<ChatRoomParticipants> receiverRooms = chatRoomParticipantsRepository.findByUser(receiver);

        for (ChatRoomParticipants creatorRoom : creatorRooms) {
            for (ChatRoomParticipants receiverRoom : receiverRooms) {
                if (creatorRoom.getChatRoom().equals(receiverRoom.getChatRoom())) {
                    // 두 사용자가 동일한 채팅방에 참여 중인 경우
                    Long existingChatRoomId = creatorRoom.getChatRoom().getId();
                    System.out.println("existingChatRoomId번호는 " + existingChatRoomId);
                  //  return ResponseEntity.ok(existingChatRoomId);
                    return ResponseEntity.ok(Map.of("status", "exists", "chatRoomId", existingChatRoomId));
                }
            }
        }



        ChatRooms chatRoom = new ChatRooms();
        chatRoom.setRoomName(roomName);
        chatRoom.setCreatedBy(createdBy);

        LocalDate localDate = LocalDate.now();
        chatRoom.setCreatedAt(localDate.atStartOfDay());

        ChatRooms result = chatRoomRepository.save(chatRoom);

        //생성한 채팅방 id
        Long chatRoomId = result.getId();
        System.out.println("생성한 채팅방 id " + chatRoomId);


        // 새 채팅방 생성
        ChatRoomParticipants chatRoomParticipants1  = new ChatRoomParticipants();
        chatRoomParticipants1.setChatRoom(result);
        chatRoomParticipants1.setUser(createdBy);
        chatRoomParticipantsRepository.save(chatRoomParticipants1);


        ChatRoomParticipants  chatRoomParticipants2 = new ChatRoomParticipants();
        chatRoomParticipants2.setChatRoom(result);
        chatRoomParticipants2.setUser(receiver);
        chatRoomParticipantsRepository.save(chatRoomParticipants2);


        //return ResponseEntity.ok(chatRoomId);
        return ResponseEntity.ok(Map.of("status", "created", "chatRoom", chatRoomId));
    }


    @GetMapping("/list")
    public ResponseEntity<Object> listChatRooms(@RequestParam("userId") Long userId) {
        System.out.println("넘어오나 ?");
        try {
            // 사용자가 참여한 채팅방 ID 목록 조회
            List<Long> chatRoomIds = chatRoomParticipantsRepository.findChatRoomIdsByUserId(userId);
            for (Long chatRoomId : chatRoomIds) {
                System.out.println("참여한 채팅방 ID: " + chatRoomId);
            }

            if (chatRoomIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("참여 중인 채팅방이 없습니다.");
            }

            // 채팅방 ID를 기준으로 채팅방 데이터 조회
            List<ChatRooms> chatRooms = chatRoomRepository.findByIdIn(chatRoomIds);
            for (ChatRooms chatRoom : chatRooms) {
                System.out.println("채팅방 데이터: " + chatRoom);
            }

            if (chatRooms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("해당 채팅방 정보를 찾을 수 없습니다.");
            }

            return ResponseEntity.ok(chatRooms);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 목록을 가져오는 중 오류가 발생했습니다.");
        }
    }
}