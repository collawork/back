package com.collawork.back.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class KakaoController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/kakao/user-info")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token) {
        String url = "https://kapi.kakao.com/v2/user/me";

        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Error fetching user info from Kakao: " + e.getMessage());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            throw e;
        }
    }
}
