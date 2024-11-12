package com.collawork.back.repository;

import com.collawork.back.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    //메세지 번호역순으로 메세지 뿌려줌
    @Query("SELECT m FROM Message m JOIN m.sender u WHERE m.chatRoomId = :chatRoomId ORDER BY m.id  ASC")
    List<Message> findMessagesWithUsernameByChatRoomId(@Param("chatRoomId") Long chatRoomId);

}
