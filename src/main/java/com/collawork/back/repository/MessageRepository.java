package com.collawork.back.repository;

import com.collawork.back.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomId(Long chatRoomId);

}
