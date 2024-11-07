package com.collawork.back.controller;

import com.collawork.back.model.Friend;
import com.collawork.back.model.Notification;
import com.collawork.back.model.User;
import com.collawork.back.repository.FriendRepository;
import com.collawork.back.repository.NotificationRepository;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String getFriendshipStatus(@RequestParam Long userId, @RequestParam Long selectedUserId) {
        Optional<Friend> friendship = friendRepository.findFriendshipBetweenUsers(userId, selectedUserId);
        System.out.println("userId : " + userId);
        System.out.println("selectedUserId : " + selectedUserId);
        System.out.println("friendship : " + friendship);
        if (friendship.isPresent()) {
            return friendship.get().getStatus().name();
        }
        return "친구가 없습니다.";
    }

    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam Long requesterId, @RequestParam Long responderId) {
        System.out.println("요청 Id : " + requesterId);
        System.out.println("반환 Id : " + responderId);
        try {
            Optional<Friend> existingFriend = friendRepository.findFriendshipBetweenUsers(requesterId, responderId);
            if (existingFriend.isPresent()) {
                Friend.Status status = existingFriend.get().getStatus();
                if (status == Friend.Status.PENDING) {
                    return "이미 친구 요청을 보냈습니다.";
                } else if (status == Friend.Status.ACCEPTED) {
                    return "이미 친구입니다.";
                }
            }

            Optional<User> requester = userRepository.findById(requesterId);
            Optional<User> responder = userRepository.findById(responderId);

            if (requester.isPresent() && responder.isPresent()) {
                Friend friend = new Friend();
                friend.setRequester(requester.get());
                friend.setResponder(responder.get());
                friend.setStatus(Friend.Status.PENDING);
                Friend savedFriend = friendRepository.save(friend); // 저장 후 ID 생성

                // 알림 생성
                Notification notification = new Notification();
                notification.setUser(responder.get());
                notification.setType(Notification.Type.FRIEND_REQUEST);
                notification.setMessage(requester.get().getUsername() + "님이 친구 요청을 보냈습니다.");
                notification.setRequestId(savedFriend.getId()); // friend의 ID를 requestId로 설정
                notificationRepository.save(notification);

                return "친구 요청을 보냈습니다.";
            }
            return "사용자 혹은 친구를 찾을 수 없습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "친구 요청 처리 중 오류 발생";
        }
    }




    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam Long requestId) {
        System.out.println("수신된 requestId: " + requestId);
        Optional<Friend> friendship = friendRepository.findById(requestId);
        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            System.out.println("찾은 친구 요청: " + friend);
            friend.setStatus(Friend.Status.ACCEPTED);
            friendRepository.save(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 수락했습니다.");
            notification.setRequestId(requestId);
            notificationRepository.save(notification);

            return "친구 요청 승인";
        }
        System.out.println("친구 요청을 찾을 수 없습니다.");
        return "친구 요청을 찾을 수 없습니다.";
    }

    @PostMapping("/reject")
    public String rejectFriendRequest(@RequestParam Long requestId) {
        Optional<Friend> friendship = friendRepository.findById(requestId);
        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            friendRepository.delete(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 거절했습니다.");
            notification.setRequestId(requestId);
            notificationRepository.save(notification);

            return "친구 요청이 거절되었습니다.";
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