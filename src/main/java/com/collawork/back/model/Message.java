package com.collawork.back.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomId;
    private String senderId;
    private String content;
    private String messageType; // "text" 또는 "file"
    private String fileUrl; // 파일 URL 추가

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Message(Long id, String chatRoomId, String senderId, String content, String messageType, String fileUrl, LocalDateTime createdAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
    }

    public Message() {
        // 기본 생성자
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // 엔티티가 저장되기 전에 현재 시간 설정
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // toString() 메서드
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}