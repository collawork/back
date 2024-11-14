package com.collawork.back.controller;

import com.collawork.back.dto.FriendRequestDTO;
import com.collawork.back.model.Friend;
import com.collawork.back.model.Notification;
import com.collawork.back.model.User;
import com.collawork.back.repository.FriendRepository;
import com.collawork.back.repository.NotificationRepository;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 작성자: 김동규 / 작성일: 2024-11-14
 * 설명: 친구관련 컨트롤러
 * */
@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "http://localhost:3000")
public class FriendController {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 친구 관계 상태 확인 메소드
     * */
    @GetMapping("/status")
    public Map<String, Object> getFriendshipStatus(@RequestParam Long userId, @RequestParam Long selectedUserId) {
        Optional<Friend> friendship = friendRepository.findFriendshipBetweenUsers(userId, selectedUserId);
        Map<String, Object> response = new HashMap<>();




        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            response.put("status", friend.getStatus().name());
            response.put("isRequester", friend.getRequester().getId().equals(userId));
            response.put("requesterId", friend.getRequester().getId());
            response.put("id", friendship.get().getId());

            System.out.println("friendship : " + friendship.get().getId());
            System.out.println("requesterId : " + friend.getRequester().getId());
            System.out.println("isRequester : " + friend.getRequester().getId().equals(userId));

        } else {
            response.put("status", "NONE");
            response.put("isRequester", false);
        }

        return response;
    }

    /**
     * 친구 요청 메소드
     * */
    @PostMapping("/request")
    public Map<String, Object> sendFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        Long requesterId = friendRequestDTO.getRequesterId();
        Long responderId = friendRequestDTO.getResponderId();

        if (requesterId == null || responderId == null) {
            response.put("message", "요청자 ID와 응답자 ID가 필요합니다.");
            return response;
        }

        try {
            Optional<User> requester = userRepository.findById(requesterId);
            Optional<User> responder = userRepository.findById(responderId);

            if (requester.isPresent() && responder.isPresent()) {
                // 친구 요청 생성 로직
                Friend friend = new Friend();
                friend.setRequester(requester.get());
                friend.setResponder(responder.get());
                friend.setStatus(Friend.Status.PENDING);
                Friend savedFriend = friendRepository.save(friend);

                // 알림 생성 로직
                Notification notification = new Notification();
                notification.setUser(responder.get());  // 알림 수신자 설정
                notification.setType(Notification.Type.FRIEND_REQUEST);
                notification.setMessage(requester.get().getUsername() + "님이 친구 요청을 보냈습니다.");
                notification.setRequestId(savedFriend.getId());
                notification.setResponderId(responderId);
                notificationRepository.save(notification);

                response.put("message", "친구 요청을 보냈습니다.");
                response.put("requestId", savedFriend.getId());
                return response;
            }
            response.put("message", "사용자 혹은 친구를 찾을 수 없습니다.");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "친구 요청 처리 중 오류 발생");
            return response;
        }
    }

    /**
     * 친구 승인 메소드
     * */
    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(
            @RequestParam Long requestId,
            @RequestParam Long responderId) {

        Optional<Friend> friendship = friendRepository.findById(requestId);

        if (friendship.isPresent()) {
            Friend friend = friendship.get();

            if (!friend.getResponder().getId().equals(responderId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }

            friend.setStatus(Friend.Status.ACCEPTED);
            friendRepository.save(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 수락했습니다.");
            notification.setRequestId(requestId);
            notification.setResponderId(responderId);
            notificationRepository.save(notification);

            return ResponseEntity.ok("친구 요청 승인");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("친구 요청을 찾을 수 없습니다.");
    }


    /**
     * 친구 요청 거절 메소드
     * */
    @PostMapping("/reject")
    public String rejectFriendRequest(@RequestParam Long requesterId, @RequestParam Long responderId) {
        System.out.println("수신된 requesterId: " + requesterId + ", responderId: " + responderId);

        if (requesterId.equals(responderId)) {
            System.out.println("에러: 자기 자신에게 친구 요청을 거절할 수 없습니다.");
            return "에러: 자기 자신에게 친구 요청을 거절할 수 없습니다.";
        }

        Optional<Friend> friendship = friendRepository.findByRequesterIdAndResponderId(requesterId, responderId);
        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            friendRepository.delete(friend);

            // 요청을 보낸 사용자에게 거절 알림 생성
            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 거절했습니다.");
            notification.setRequestId(friend.getId());
            notification.setResponderId(responderId);
            notification.setActionCompleted(true);
            notificationRepository.save(notification);

            return "친구 요청이 거절되었습니다.";
        } else {
            System.out.println("친구 요청을 찾을 수 없습니다.");
            return "친구 요청을 찾을 수 없습니다.";
        }
    }

    /**
     * 친구 삭제하는 메소드
     * */
    @DeleteMapping("/remove")
    public String removeFriend(@RequestParam Long requestId) {
        friendRepository.deleteById(requestId);

        System.out.println("친구 삭제가 성공적으로 완료되었습니다. requestId: " + requestId);

        return "친구 삭제가 성공적으로 완료되었습니다.";
    }

    /**
     * 친구 목록 보는 메소드
     * */
    @GetMapping("/list")
    public List<Friend> getFriends(@RequestParam Long userId) {
        System.out.println("친구 목록 요청 userId: " + userId);
        return friendRepository.findFriendsByUserIdAndStatus(userId, Friend.Status.ACCEPTED);
    }

    /**
     * 요청 목록을 보기 위한 메소드
     * */
    @GetMapping("/request")
    public ResponseEntity<Map<String, Object>> getFriendRequest(@RequestParam Long requestId) {
        Optional<Friend> friendship = friendRepository.findById(requestId);
        Map<String, Object> response = new HashMap<>();

        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            response.put("requesterId", friend.getRequester().getId());
            response.put("responderId", friend.getResponder().getId());
            response.put("status", friend.getStatus().name());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "친구 요청을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}