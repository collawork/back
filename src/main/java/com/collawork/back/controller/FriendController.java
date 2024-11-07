package com.collawork.back.controller;

import com.collawork.back.model.Friend;
import com.collawork.back.model.Notification;
import com.collawork.back.model.User;
import com.collawork.back.repository.FriendRepository;
import com.collawork.back.repository.NotificationRepository;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/status")
    public Map<String, Object> getFriendshipStatus(@RequestParam Long userId, @RequestParam Long selectedUserId) {
        Optional<Friend> friendship = friendRepository.findFriendshipBetweenUsers(userId, selectedUserId);
        Map<String, Object> response = new HashMap<>();

        if (friendship.isPresent()) {
            response.put("status", friendship.get().getStatus().name());
            response.put("isRequester", friendship.get().getRequester().getId().equals(userId));
        } else {
            response.put("status", "NONE");
            response.put("isRequester", false);
        }

        return response;
    }


    @PostMapping("/request")
    public Map<String, Object> sendFriendRequest(@RequestParam Long requesterId, @RequestParam Long responderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Friend> existingFriend = friendRepository.findFriendshipBetweenUsers(requesterId, responderId);
            if (existingFriend.isPresent()) {
                Friend.Status status = existingFriend.get().getStatus();
                response.put("requestId", existingFriend.get().getId());
                if (status == Friend.Status.PENDING) {
                    response.put("message", "이미 친구 요청을 보냈습니다.");
                    return response;
                } else if (status == Friend.Status.ACCEPTED) {
                    response.put("message", "이미 친구입니다.");
                    return response;
                }
            }

            Optional<User> requester = userRepository.findById(requesterId);
            Optional<User> responder = userRepository.findById(responderId);

            if (requester.isPresent() && responder.isPresent()) {
                Friend friend = new Friend();
                friend.setRequester(requester.get());
                friend.setResponder(responder.get());
                friend.setStatus(Friend.Status.PENDING);
                Friend savedFriend = friendRepository.save(friend);

                // 알림 생성: 수신자(responder)가 알림을 받음
                Notification notification = new Notification();
                notification.setUser(responder.get()); // 알림의 수신자를 responder로 설정
                notification.setType(Notification.Type.FRIEND_REQUEST);
                notification.setMessage(requester.get().getUsername() + "님이 친구 요청을 보냈습니다.");
                notification.setRequestId(savedFriend.getId());
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


    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam Long requesterId, @RequestParam Long responderId) {
        // 같은 ID로 친구 요청 수락 방지
        if (requesterId.equals(responderId)) {
            System.out.println("에러: 자기 자신에게 친구 요청을 수락할 수 없습니다.");
            return "에러: 자기 자신에게 친구 요청을 수락할 수 없습니다.";
        }

        // 친구 요청 조회
        System.out.println("수신된 requesterId: " + requesterId + ", responderId: " + responderId);
        Optional<Friend> friendship = friendRepository.findByRequesterIdAndResponderId(requesterId, responderId);

        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            System.out.println("찾은 친구 요청: " + friend);

            friend.setStatus(Friend.Status.ACCEPTED);
            friendRepository.save(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 수락했습니다.");
            notificationRepository.save(notification);

            return "친구 요청 승인";
        } else {
            System.out.println("에러: 친구 요청을 찾을 수 없습니다. requesterId: " + requesterId + ", responderId: " + responderId);
            return "친구 요청을 찾을 수 없습니다.";
        }
    }

    @PostMapping("/reject")
    public String rejectFriendRequest(@RequestParam Long requesterId, @RequestParam Long responderId) {
        System.out.println("수신된 requesterId: " + requesterId + ", responderId: " + responderId);

        Optional<Friend> friendship = friendRepository.findByRequesterIdAndResponderId(requesterId, responderId);
        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            friendRepository.delete(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 거절했습니다.");
            notificationRepository.save(notification);

            return "친구 요청이 거절되었습니다.";
        } else {
            System.out.println("친구 요청을 찾을 수 없습니다. requesterId와 responderId로 조회 실패");
        }
        return "친구 요청을 찾을 수 없습니다.";
    }


    @DeleteMapping("/remove")
    public String removeFriend(@RequestParam Long requestId) {
        friendRepository.deleteById(requestId);
        return "친구삭제가 성공적으로 완료되었습니다.";
    }

    @GetMapping("/list")
    public List<Friend> getFriends(@RequestParam Long userId) {
        return friendRepository.findFriendsByUserIdAndStatus(userId, Friend.Status.ACCEPTED);
    }

}