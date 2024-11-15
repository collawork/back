package com.collawork.back.repository;

import com.collawork.back.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m JOIN m.sender u WHERE m.chatRoomId = :chatRoomId ORDER BY m.id  ASC")
    List<Message> findMessagesWithUsernameByChatRoomId(@Param("chatRoomId") Long chatRoomId);

}
