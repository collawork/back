package com.collawork.back.repository;

import com.collawork.back.model.ChatRooms;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRooms,Long> {

    @Query("SELECT c.roomName FROM ChatRooms c WHERE c.id = :id")
    String findChatRoomNameById(@Param("id") Long id);

}
