package com.collawork.back.repository;

import com.collawork.back.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomId(String chatRoomId); // 특정 채팅방 ID에 해당하는 메시지 조회
}