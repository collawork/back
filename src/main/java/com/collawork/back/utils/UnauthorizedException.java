package com.collawork.back.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // HTTP 401 응답 코드 반환
public class UnauthorizedException extends RuntimeException {

    // 기본 생성자
    public UnauthorizedException(String message) {
        super(message);
    }

    // 메시지와 원인(cause)을 함께 처리할 경우 사용
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
