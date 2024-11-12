package com.collawork.back.model;

import com.collawork.back.repository.MessageType;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User sender;

    @Column(name = "sender_id")
    private Long senderId;

    private Long chatRoomId;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String fileUrl;
    private Date createdAt;

    // 기본 생성자
    public Message() {
    }

    // 생성자
    public Message(Long id, Long senderId, Long chatRoomId, String content, MessageType messageType, String fileUrl, Date createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.messageType = messageType;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + sender +
                ", chatRoomId=" + chatRoomId +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                ", fileUrl='" + fileUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}