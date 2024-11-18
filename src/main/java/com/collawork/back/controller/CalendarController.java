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
import java.time.ZonedDateTime;
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
        System.out.println("0");
        scheduleInfo.setStart(ZonedDateTime.parse(data.get("start").toString()));
        if(data.get("end").toString() == null || data.get("end").toString().isEmpty()){
            scheduleInfo.setEnd(ZonedDateTime.parse(data.get("start").toString()));
        }else {
            scheduleInfo.setEnd(ZonedDateTime.parse(data.get("end").toString()));
        }
        System.out.println("1");
        scheduleInfo.setCreatedAt(ZonedDateTime.now());
        System.out.println("2");
        scheduleInfo.setCreatedBy(new BigInteger(Integer.toString((Integer)data.get("createdBy"))));
        System.out.println("3");
        scheduleInfo.setAllDay(data.get("allDay").toString().equals("true"));
        System.out.println("4");
        if(data.get("projectId") == null || data.get("projectId").toString().isEmpty()){
            scheduleInfo.setProjectId(null);
        }else {
            scheduleInfo.setProjectId(new BigInteger(Integer.toString((Integer)data.get("projectId"))));
        }
        System.out.println("scheduleInfo.getProjectId() = " + scheduleInfo.getProjectId());

        System.out.println("scheduleInfo.isAllDay() = " + scheduleInfo.isAllDay());




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
