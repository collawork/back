package com.collawork.back.service;

import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(username));

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        User user = userOptional.get();

        // 비밀번호가 null인 경우 빈 비밀번호 설정
        String password = user.getPassword() != null ? user.getPassword() : "{noop}";

        UserBuilder builder = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .roles("USER");

        return builder.build();
    }
}
