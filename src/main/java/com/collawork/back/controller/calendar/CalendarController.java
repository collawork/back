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
        System.out.println("CalendarController: 프로젝트 아이디로 이벤트를 찾는 메소드:::::::::::::::::::::::::::");

        Long longValueNull = null;
        List<CalendarDTO> result;
        System.out.println("data : "+data);
        System.out.println("data.getClass().getName() = " + data.getClass().getName());
        if(data.equals("0")){
            System.out.println("1");
            result = calendarService.eventsByProjectId(longValueNull);
            System.out.println("2");
        }else{
            System.out.println("3");
            result = calendarService.eventsByProjectId(Long.parseLong(data));
            System.out.println("4");
        }
        System.out.println("5");
//        if(data != null) {
//            try {
//                longValue = Long.parseLong(data);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//        }else {
//            System.out.println("The string is null and cannot be converted to Long.");
//        }
//            System.out.println("1111111111111111111111111111");
//            data = "";
//            System.out.println("222222222222222222222222222");

        // List<CalendarDTO> result = calendarService.eventsByProjectId(Long.parseLong(data));

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

        System.out.println("test::::::::::::::::: "+rawData);

        Map<String, Object> data = (Map<String, Object>)rawData.get("newData");
        System.out.println("data = " + data);
        ExtendedProps extendedProps = new ExtendedProps();
        CalendarDTO scheduleInfo = new CalendarDTO();

        System.out.println("insert 시작");

        if(data.get("description") == null) {
            System.out.println("1");
            extendedProps.setDescription(null);
            System.out.println("2");
        }else {
            System.out.println("3");
            extendedProps.setDescription(data.get("description").toString());
            System.out.println("4");
        }
        System.out.println("5");
        extendedProps.setCreatedAt(ZonedDateTime.now());
        System.out.println("6");
        extendedProps.setCreatedBy(Long.parseLong((String) data.get("createdBy")));

        //extendedProps.setCreatedBy(new BigInteger(Integer.toString((Integer)data.get("createdBy"))));
        // scheduleInfo.setGroupId(new BigInteger(Integer.toString((Integer)data.get("groupId"))));
        if(data.containsKey("projectId") && data.get("projectId") != null){
            System.out.println("8");
            extendedProps.setProjectId((Long) data.get("projectId"));
        }else {
            System.out.println("7");
            extendedProps.setProjectId(null);
        }



        System.out.println("7");
        if(data.get("title") == null) {
            System.out.println("1");
            scheduleInfo.setTitle(null);
            System.out.println("2");
        }else {
            System.out.println("3");
            scheduleInfo.setTitle(data.get("title").toString());
            System.out.println("4");
        }
        // scheduleInfo.setTitle(data.get("title").toString());

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


        try {
            ZonedDateTime start = ZonedDateTime.parse((String) data.get("start"));
            ZonedDateTime end = ZonedDateTime.parse((String) data.get("end"));
            System.out.println("zone ::::::::::" + start);
            System.out.println(end);
            scheduleInfo.setStart(start);
            scheduleInfo.setEnd(end);
        }catch (DateTimeParseException e){
            
            try {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                LocalDate start = LocalDate.parse((String) data.get("start"));
                LocalDate end = LocalDate.parse((String) data.get("end"));
                System.out.println("local::::::::::::"+start);
                System.out.println(end);

                ZonedDateTime zoneStart = start.atStartOfDay(zoneId);
                ZonedDateTime zoneEnd = end.atStartOfDay(zoneId);

                System.out.println(zoneStart);
                System.out.println(zoneEnd);

                scheduleInfo.setStart(zoneStart);
                scheduleInfo.setEnd(zoneEnd);
            }catch (DateTimeParseException e2){
                System.out.println("아무것도 안담김");
                return null;
            }
           
            
        }

        System.out.println("03");
        scheduleInfo.setAllDay(data.get("allDay").toString().equals("true"));
        System.out.println("04");
        if(data.get("groupId") == null || data.get("groupId").toString().isEmpty()){
            extendedProps.setProjectId(null);
        }else {
            extendedProps.setProjectId((Long) data.get("groupId"));
        }


        System.out.println("scheduleInfo.isAllDay() = " + scheduleInfo.isAllDay());



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
        System.out.println("CalendarController: 아이디로 특정 스케쥴을 특정하고 해당 일정을 수정하는 메소드::::::::::::::::::::");

        Map<String, Object> data = (Map<String, Object>)rawData.get("updateData");

        BigInteger id = new BigInteger(String.valueOf(data.get("id")));
        String title = (String) data.get("title");
        String description = (String) data.get("description");

        System.out.println("id = ::::::::::::::::::::::::::::::" + id);

        boolean result = calendarService.updateSelectedEvent(id, title, description);

        return ResponseEntity.ok(result);
    }
}
