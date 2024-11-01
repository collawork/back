package com.collawork.back.controller;


import com.collawork.back.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/chat/*")
public class ChatRoomController {

    @Autowired
    private ChatService chatService;

    @GetMapping("invite")
    public ModelAndView chatInvite(ModelAndView mv){
        return  mv;
    }

}
