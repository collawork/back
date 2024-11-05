package com.collawork.back.controller;

import com.collawork.back.model.Friend;
import com.collawork.back.model.Notification;
import com.collawork.back.model.User;
import com.collawork.back.repository.FriendRepository;
import com.collawork.back.repository.NotificationRepository;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> responder = userRepository.findById(responderId);

        if (requester.isPresent() && responder.isPresent()) {
            Friend friend = new Friend();
            friend.setRequester(requester.get());
            friend.setResponder(responder.get());
            friendRepository.save(friend);

            Notification notification = new Notification();
            notification.setUser(responder.get());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(requester.get().getUsername() + "님이 친구 요청을 보냈습니다.");
            notificationRepository.save(notification);

            return "친구가 요청을 보냈습니다.";
        }
        return "사용자 혹은 친구를 찾을 수 없습니다.";
    }

    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam Long requestId) {
        Optional<Friend> friendship = friendRepository.findById(requestId);
        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            friend.setStatus(Friend.Status.ACCEPTED);
            friendRepository.save(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 수락했습니다.");
            notificationRepository.save(notification);

            return "친구 요청 승인";
        }
        return "친구 요청을 찾을 수 없습니다.";
    }

    @PostMapping("/reject")
    public String rejectFriendRequest(@RequestParam Long requestId) {
        Optional<Friend> friendship = friendRepository.findById(requestId);
        if (friendship.isPresent()) {
            Friend friend = friendship.get();
            friend.setStatus(Friend.Status.REJECTED);
            friendRepository.save(friend);

            Notification notification = new Notification();
            notification.setUser(friend.getRequester());
            notification.setType(Notification.Type.FRIEND_REQUEST);
            notification.setMessage(friend.getResponder().getUsername() + "님이 친구 요청을 거절했습니다.");
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
}