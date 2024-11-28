package com.collawork.back.repository;

import com.collawork.back.model.ChatRoomParticipants;
import com.collawork.back.model.auth.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRoomParticipantsRepository extends CrudRepository<ChatRoomParticipants, Long> {
    List<ChatRoomParticipants> findByUser(User createdBy);

    @Query("SELECT p.chatRoom.id FROM ChatRoomParticipants p WHERE p.user.id = :userId")
    List<Long> findChatRoomIdsByUserId(@Param("userId") Long userId);
}
