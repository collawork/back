package com.collawork.back.service;

import com.collawork.back.dto.MessageDTO;
import com.collawork.back.model.Message;
import com.collawork.back.repository.MessageRepository;
import com.collawork.back.repository.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void saveMessage(MessageDTO messageDTO) throws ParseException {

        if (messageDTO.getTime() == null) {
            System.err.println("메시지 시간 값이 null입니다.");
            return;  // null인 경우 메시지를 저장하지 않음
        }

        System.out.println(messageDTO.getMessage());
        System.out.println(messageDTO.getSenderId());
        System.out.println(messageDTO.getChatRoomId());
        System.out.println(messageDTO.getType());
        System.out.println(messageDTO.getTime());  //오후 5:02:25
        System.out.println(messageDTO.getFileUrl());
        String sdate = messageDTO.getTime();

       SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm:ss");
      Date date = sdf.parse(sdate);
      Timestamp timestamp = new Timestamp(date.getTime());
       System.out.println(timestamp);

            Message message = new Message();
            message.setSenderId(Long.parseLong(messageDTO.getSenderId()));
            message.setChatRoomId(Long.parseLong(messageDTO.getChatRoomId()));
            message.setContent(messageDTO.getMessage());
            message.setMessageType(MessageType.valueOf(messageDTO.getType().toUpperCase()));
            message.setFileUrl(messageDTO.getFileUrl());
            message.setCreatedAt(timestamp);
            System.out.println(messageDTO.getMessage());
            messageRepository.save(message);

    }

    public List<Message> getMessagesByChatRoomId(Long chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }

}
