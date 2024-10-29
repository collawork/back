package com.collawork.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
* 작성자 : 김동규 / 2024-10-29 최초작성
* 설명 : 비밀번호 암호화 및 소셜 로그인 설정
* */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 인증 없이 접근 가능
                        .anyRequest().authenticated() // 그 외의 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 소셜 로그인 설정
                        .loginPage("/api/auth/login") // 로그인 페이지 설정 (Optional)
                        .defaultSuccessUrl("/api/auth/success", true) // 로그인 성공 시 이동할 URL
                );
        return http.build();
    }
}
