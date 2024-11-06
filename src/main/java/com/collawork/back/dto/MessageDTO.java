package com.collawork.back.dto;

import java.time.LocalDateTime;

public class MessageDTO {

    private int id;                 // 메시지 고유 ID
    private String senderId;           // 보낸 사용자 ID
    private String chatRoomId;         // 채팅방 ID
    private String content;          // 메시지 내용
    private String messageType;      // 메시지 유형 ('text', 'image', 'file')
    private String fileUrl;          // 파일 URL
    private LocalDateTime createdAt; // 메시지 보낸 시간
    private String type;

    public MessageDTO() {
    }

    public MessageDTO(int id, String senderId, String chatRoomId, String content, String messageType, String fileUrl, LocalDateTime createdAt, String type) {
        this.id = id;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.messageType = messageType;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", senderId='" + senderId + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", createdAt=" + createdAt +
                ", type='" + type + '\'' +
                '}';
    }
}
