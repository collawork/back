package com.collawork.back.controller;

import com.collawork.back.dto.CalendarDTO;
import com.collawork.back.model.Calendar;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.CalendarService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(("/api/calendar"))
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/insert")
    public ResponseEntity<Object> insert(@RequestBody Map<String,Object> data, HttpServletRequest request) {

        System.out.println("test::::::::::::::::: "+data);

        CalendarDTO scheduleInfo = new CalendarDTO();
        scheduleInfo.setTitle(data.get("title").toString());
        scheduleInfo.setDescription(data.get("description").toString());
        
        scheduleInfo.setCreateBy(new BigInteger((String) data.get("createBy")));
//        // data에서 createBy 값을 가져옴
//        String createByString = (String) data.get("createBy");
//        // String을 BigInteger로 변환
//        BigInteger createBy = new BigInteger(createByString);
//        // scheduleInfo에 createBy 설정
//        scheduleInfo.setCreateBy(createBy);
        scheduleInfo.setProjectId(new BigInteger((String) data.get("projectId")));
        scheduleInfo.setStart(LocalDateTime.parse(data.get("start").toString()));
        scheduleInfo.setEnd(LocalDateTime.parse(data.get("end").toString()));



        // 토큰..
        String token = request.getHeader("Authorization");
        System.out.println("token : " + token);
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        // DB에 일정 등록하기 - 서비스로 POST요청 처리 이관..
        try {
            Optional<Calendar> result = calendarService.insertSchedule(scheduleInfo);
            if (result.isEmpty()) {
                return ResponseEntity.status(403).body("입력된 일정이 없습니다.");
            }
            return ResponseEntity.ok("일정 등록에 성공하였습니다.");
        }catch (Exception e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
        // return ResponseEntity.ok("test");
    }
}
