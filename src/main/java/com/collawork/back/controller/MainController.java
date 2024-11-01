package com.collawork.back.controller;

import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class MainController {

    private final UserRepository userRepository;

    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user/info")
    public User getUserInfo(@RequestParam String username) {
        return userRepository.findByUsername(username);
    }

}
