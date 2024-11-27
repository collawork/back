package com.collawork.back.model;


import com.collawork.back.model.auth.User;
import jakarta.persistence.*;

@Entity
@Table(name = "chat_room_participants")
public class ChatRoomParticipants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "chat_room_id", foreignKey = @ForeignKey(name = "fk_chat_room_participants_chat_room_id"))
    private ChatRooms chatRoom;


    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_chat_room_participants_user_id"))
    private User user;

    public ChatRoomParticipants() {
    }

    public ChatRoomParticipants(Long id, ChatRooms chatRoom, User user) {
        this.id = id;
        this.chatRoom = chatRoom;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatRooms getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRooms chatRoom) {
        this.chatRoom = chatRoom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ChatRoomParticipants{" +
                "id=" + id +
                ", chatRoom=" + chatRoom +
                ", user=" + user +
                '}';
    }
}
