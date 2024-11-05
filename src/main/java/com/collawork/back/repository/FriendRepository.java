package com.collawork.back.repository;

import com.collawork.back.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 두 사용자 간의 친구 관계를 찾기 위한 메소드
    Optional<Friend> findByRequesterIdAndResponderId(Long requesterId, Long responderId);

    // 역방향으로도 친구 관계를 찾기 위한 메소드
    Optional<Friend> findByResponderIdAndRequesterId(Long responderId, Long requesterId);

    // 특정 요청자와 응답자 간의 친구 관계를 찾기 위한 메소드
    default Optional<Friend> findFriendshipBetweenUsers(Long userId1, Long userId2) {
        Optional<Friend> friendship = findByRequesterIdAndResponderId(userId1, userId2);
        if (friendship.isEmpty()) {
            friendship = findByResponderIdAndRequesterId(userId1, userId2);
        }
        return friendship;
    }
}
