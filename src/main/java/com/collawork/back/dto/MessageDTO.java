package com.collawork.back.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.Date;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {

    private String senderId; // 보낸 사용자 ID
    private String chatRoomId; // 채팅방 ID
    private String message; // 메시지 내용
    private String messageType; // 메시지 유형 ('text', 'image', 'file')
    private String fileUrl; // 파일 URL
    private String time; // 메시지 보낸 시간

    public MessageDTO() {
    }

    public MessageDTO(String senderId, String chatRoomId, String message, String messageType, String fileUrl, String time) {
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.messageType = messageType;
        this.fileUrl = fileUrl;
        this.time = time;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "senderId='" + senderId + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", message='" + message + '\'' +
                ", messageType='" + messageType + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}