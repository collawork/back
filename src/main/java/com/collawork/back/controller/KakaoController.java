package com.collawork.back.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
public class KakaoController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/kakao/user-info")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return response.getBody();  // 사용자 정보 반환
        } catch (HttpClientErrorException e) {
            // 401 오류나 다른 예외 발생 시 로그 출력
            System.err.println("Error fetching user info from Kakao: " + e.getMessage());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            throw e;  // 필요 시 클라이언트에 예외 전파
        }
    }
}
