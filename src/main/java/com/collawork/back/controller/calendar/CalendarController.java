package com.collawork.back.controller.calendar;

import com.collawork.back.dto.calendar.CalendarDTO;
import com.collawork.back.dto.calendar.ExtendedProps;
import com.collawork.back.model.calendar.Calendar;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.calendar.CalendarService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(("/api/calendar"))
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/events")
    public ResponseEntity<Object> EventsByProjectId(@RequestParam("selectedProjectId") String data, HttpServletRequest request) {
        System.out.println("CalendarController: 프로젝트 아이디로 스케쥴 리스트를 찾는 메소드:::::::::::::::::::::::::::");

        // 결과를 받을 DTO 리스트, 프론트에서 사용하는 Fullcalendar API가 제공해 주는 기능과 DB의 컬럼명이 상이하여
        // 엔티티로 받은 값을 다시 DTO에 담아서 프론트로 보내준다.
        List<CalendarDTO> result;

        // 프로젝트 아이디가 있으면 프로젝트에 속한 스케쥴이고, 없다면 개인 스케쥴이다.
        // 하나의 달력 컴포넌트로 프로젝트 달력과 개인 달력을 출력할 수 있도록 했다.
        Long longValueNull = null;
        if(data.equals("0")){
            result = calendarService.eventsByProjectId(longValueNull);
        }else{
            result = calendarService.eventsByProjectId(Long.parseLong(data));
        }

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
        System.out.println("result!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! = " + result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/insert")
    public ResponseEntity<Object> insert(@RequestBody Map<String,Object> rawData, HttpServletRequest request) {
        System.out.println("CalendarController: 스케쥴을 DB에 저장하는 메소드:::::::::::::::::::::::::::");

        Map<String, Object> data = (Map<String, Object>)rawData.get("newData"); // 프론트에서 객체를 감싸서, 백에서 껍질을 깠다.

        CalendarDTO scheduleInfo = new CalendarDTO();
        ExtendedProps extendedProps = new ExtendedProps(); // CalendarDTO에 존재하는 필드 클래스, 모두 담은 후 밑에서 합칠 예정.

        // 스케쥴의 상세 내용을 DTO에 담고 있다.
        if(data.get("description") == null) {
            extendedProps.setDescription(null);
        }else {
            extendedProps.setDescription(data.get("description").toString());
        }
        // 스케쥴의 생성일
        extendedProps.setCreatedAt(ZonedDateTime.now());
        // 스케쥴을 등록한 유저 ID (DB에 저장된 고유값)
        Long createdBy = Long.valueOf(data.get("createdBy").toString());
        extendedProps.setCreatedBy(createdBy);
        // 스케쥴의 소속 (해당 스케쥴의 프로젝트 아이디)
        if(data.containsKey("projectId") && data.get("projectId") != null){
            extendedProps.setProjectId(Long.parseLong(data.get("projectId").toString()));
        }else {
            extendedProps.setProjectId(null);
        }
        // 스케쥴의 제목
        if(data.get("title") == null) {
            scheduleInfo.setTitle(null);
        }else {
            scheduleInfo.setTitle(data.get("title").toString());
        }
        // 스케쥴의 시작 시점과 종료 시점
        // 시분(00:00)이 있는 정보는 ZonedDateTime에 그대로 담고, 그렇지 않은 데이터는 붙여서 담는다.
        try {
            ZonedDateTime start = ZonedDateTime.parse((String) data.get("start"));
            ZonedDateTime end = ZonedDateTime.parse((String) data.get("end"));

            scheduleInfo.setStart(start);
            scheduleInfo.setEnd(end);
        }catch (DateTimeParseException e){
            try {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                LocalDate start = LocalDate.parse((String) data.get("start"));
                LocalDate end = LocalDate.parse((String) data.get("end"));

                ZonedDateTime zoneStart = start.atStartOfDay(zoneId);
                ZonedDateTime zoneEnd = end.atStartOfDay(zoneId);

                scheduleInfo.setStart(zoneStart);
                scheduleInfo.setEnd(zoneEnd);
            }catch (DateTimeParseException e2){
                return null;
            }
        }
        // 스케쥴의 allDay 여부 (true면 정확한 시분 정보가 있어도 종일 스케쥴로 표기된다.)
        scheduleInfo.setAllDay(data.get("allDay").toString().equals("true"));

        // CalendarDTO에 ExtendedProps를 담는다.
        scheduleInfo.setExtendedProps(extendedProps);




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
            Optional<Calendar> result = calendarService.insertSchedule(scheduleInfo, data);
            if (result.isEmpty()) {
                return ResponseEntity.status(403).body("입력된 일정이 없습니다.");
            }
            return ResponseEntity.ok("일정 등록에 성공하였습니다.");
        }catch (Exception e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
        // return ResponseEntity.ok("test");
    }

    @PostMapping("/update")
    public ResponseEntity<Object> update(@RequestBody Map<String,Object> rawData, HttpServletRequest request) {
        System.out.println("CalendarController: 아이디로 특정 스케쥴을 특정하고 제목 설명을 수정하는 메소드::::::::::::::::::::");

        Map<String, Object> data = (Map<String, Object>)rawData.get("updateData"); // 프론트에서 객체를 감싸서, 백에서 껍질을 깠다.

        // 데이터를 알맞은 형으로 변환
        BigInteger id = new BigInteger(String.valueOf(data.get("id")));
        String title = (String) data.get("title");
        String description = (String) data.get("description");

        // 서비스로 형변환한 데이터 전달
        boolean result = calendarService.updateSelectedEvent(id, title, description);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/updatedate")
    public ResponseEntity<Object> updatedate(@RequestBody Map<String,Object> rawData, HttpServletRequest request) {
        System.out.println("CalendarController: 아이디로 특정 스케쥴을 특정하고 날짜를 수정하는 메소드::::::::::::::::::::");

        Map<String, Object> data = (Map<String, Object>)rawData.get("updateData"); // 프론트에서 객체를 감싸서, 백에서 껍질을 깠다.

        // 알맞은 형으로 타입 변환 중..
        BigInteger id = new BigInteger(String.valueOf(data.get("id")));
        boolean allDay = data.get("allDay").toString().equals("true");

        // allDay 여부로 스케줄 시종의 타입을 전환하고, 해다 allDay 여부까지 서비스로 전달한다.
        if(!allDay){
            ZonedDateTime start = ZonedDateTime.parse((String) data.get("start"));
            ZonedDateTime end = ZonedDateTime.parse((String) data.get("end"));

            boolean result = calendarService.updateDateSelectedEvent(id, start, end, allDay);
            return ResponseEntity.ok(result);
        }else{
            ZoneId zoneId = ZoneId.of("Asia/Seoul"); // 시분이 없는 날짜 정보에 시분을 붙이기 위한 변수.

            LocalDate localDatestart = LocalDate.parse((String) data.get("start"));
            LocalDate localDateend = LocalDate.parse((String) data.get("end"));

            ZonedDateTime start = localDatestart.atStartOfDay(zoneId);
            ZonedDateTime end = localDateend.atStartOfDay(zoneId);

            boolean result = calendarService.updateDateSelectedEvent(id, start, end, allDay);
            return ResponseEntity.ok(result);
        }
    }
}
