package com.collawork.back.service.calendar;


import com.collawork.back.dto.calendar.CalendarDTO;
import com.collawork.back.dto.calendar.ExtendedProps;
import com.collawork.back.model.calendar.Calendar;
import com.collawork.back.repository.calendar.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public boolean insertSchedule(CalendarDTO scheduleInfo, Map<String, Object> data) {
        System.out.println("CalendarService: 스케쥴을 DB에 저장하는 메소드:::::::::::::::::::::::::::::::::::");

        // 달력 엔티티
        Calendar calendar = new Calendar();

        System.out.println("scheduleInfo = " + scheduleInfo);
        calendar.setDescription(scheduleInfo.getExtendedProps().getDescription());
        calendar.setCreatedAt(scheduleInfo.getExtendedProps().getCreatedAt());
        calendar.setCreatedBy(scheduleInfo.getExtendedProps().getCreatedBy());
        calendar.setProjectId(scheduleInfo.getExtendedProps().getProjectId());

        calendar.setTitle(scheduleInfo.getTitle());
        calendar.setStartTime(scheduleInfo.getStart());
        calendar.setEndTime(scheduleInfo.getEnd());
        calendar.setAllDay(scheduleInfo.isAllDay());
        calendar.setColor(scheduleInfo.getColor());

        Calendar insertedCalendar = calendarRepository.save(calendar);
        if (insertedCalendar == null) {
            return false;
        }else {
            return true;
        }
    }


    public List<CalendarDTO> eventsByProjectId(Long data, Long userId) {
        System.out.println("CalendarService: 프로젝트 아이디로 이벤트를 찾는 메소드:::::::::::::::::::::::::::");

        CalendarDTO calendarDTO;
        ExtendedProps extendedProps;
        List<CalendarDTO> calendarDTOList = new ArrayList<>();
        List<Calendar> scheduleListByProjectId = calendarRepository.findByProjectId(data);
        List<Calendar> scheduleList = new ArrayList<>();
        if(data == null){
            for (Calendar calendar : scheduleListByProjectId) {
                if(calendar.getCreatedBy().equals(userId)){
                    scheduleList.add(calendar);
                }
            }
        }else {scheduleList.addAll(scheduleListByProjectId);}

        // List<Calendar> scheduleListByUserId = calendarRepository.findByCreatedBy(userId);
        if (scheduleList.isEmpty()) {
            return null;
        }

        // 프로젝트 아이디로 조회된 정보 곧장 프로트에서 뿌려져 실시간으로 사용자에게 전달된다. 스케쥴을 복수일 테니 List로 받는다.
        for(Calendar schedule : scheduleList){
            calendarDTO = new CalendarDTO();
            extendedProps = new ExtendedProps();

            extendedProps.setCreatedAt(schedule.getCreatedAt());
            extendedProps.setCreatedBy(schedule.getCreatedBy());
            extendedProps.setDescription(schedule.getDescription());
            extendedProps.setProjectId(schedule.getProjectId());

            calendarDTO.setTitle(schedule.getTitle());
            calendarDTO.setStart(schedule.getStartTime());
            calendarDTO.setEnd(schedule.getEndTime());
            calendarDTO.setAllDay(schedule.isAllDay());
            calendarDTO.setId(schedule.getId());

            calendarDTO.setColor(schedule.getColor());


            calendarDTO.setExtendedProps(extendedProps);
            calendarDTOList.add(calendarDTO);
        }
        return calendarDTOList;

    }

    public boolean updateSelectedEvent(Long id, String title, String description, String color) {
        System.out.println("CalendarService: 아이디로 특정 스케쥴을 특정하고 제목 설명을 수정하는 메소드:::::::::::::::::::::::::::");

        // 먼저 아이디로 특정 행을 조회한 후 해당 엔티티를 받아 온다.
        Calendar selectedEvent = calendarRepository.findById(id).orElse(null);
        // 그리고 수정..
        selectedEvent.setTitle(title);
        selectedEvent.setDescription(description);
        selectedEvent.setColor(color);
        // 저장
        Calendar updateEvent = calendarRepository.save(selectedEvent);

        if(updateEvent == null){
            return false;
        }
        return true;
    }

    public boolean updateDateSelectedEvent(Long id, ZonedDateTime start, ZonedDateTime end, boolean allDay) {
        System.out.println("CalendarService: 아이디로 특정 스케쥴을 특정하고 날짜를 수정하는 메소드::::::::::::::::::::");
        // 날짜 정보를 처리하는 메소드

        Calendar selectedEvent = calendarRepository.findById(id).orElse(null);

        selectedEvent.setStartTime(start);
        selectedEvent.setEndTime(end);
        selectedEvent.setAllDay(allDay);
        Calendar updateEvent = calendarRepository.save(selectedEvent);
        if(updateEvent == null){
            return false;
        }
        return true;
    }

    public boolean deleteById(long id) {
        System.out.println("CalendarService: 아이디로 특정 스케쥴을 삭제하는 메소드::::::::::::::::::::");

        Calendar selectedCalendar = calendarRepository.findById(id).orElse(null);
        if(selectedCalendar == null){
            return false;
        }else {
            calendarRepository.delete(selectedCalendar);
            return true;
        }
    }
}
