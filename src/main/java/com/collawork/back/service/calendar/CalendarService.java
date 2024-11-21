package com.collawork.back.service.calendar;


import com.collawork.back.dto.calendar.CalendarDTO;
import com.collawork.back.dto.calendar.ExtendedProps;
import com.collawork.back.model.calendar.Calendar;
import com.collawork.back.repository.calendar.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Optional<Calendar> insertSchedule(CalendarDTO scheduleInfo, Map<String, Object> data) {
        System.out.println("CalendarService: 스케쥴을 DB에 저장하는 메소드:::::::::::::::::::::::::::::::::::");

        Calendar calendar = new Calendar();

        calendar.setDescription(scheduleInfo.getExtendedProps().getDescription());
        calendar.setCreatedAt(scheduleInfo.getExtendedProps().getCreatedAt());
        calendar.setCreatedBy(scheduleInfo.getExtendedProps().getCreatedBy());
        calendar.setProjectId(scheduleInfo.getExtendedProps().getProjectId());

        calendar.setTitle(scheduleInfo.getTitle());
        calendar.setStartTime(scheduleInfo.getStart());
        calendar.setEndTime(scheduleInfo.getEnd());
        calendar.setAllDay(scheduleInfo.isAllDay());


        if(calendarRepository.save(calendar) != null){
            return Optional.of(calendar);
        }
        return Optional.empty();
    }


    public List<CalendarDTO> eventsByProjectId(Long data) {
        System.out.println("CalendarService: 프로젝트 아이디로 이벤트를 찾는 메소드:::::::::::::::::::::::::::");

        // Long projectId;
        // List<Calendar> scheduleList;
        CalendarDTO calendarDTO;
        ExtendedProps extendedProps;
        List<CalendarDTO> calendarDTOList = new ArrayList<>();

       // System.out.println("data.getClass().getName() = " + data.getClass().getName());
        // projectId = (Long) data;
//        if (data.toString().equals("null")) {
//            System.out.println("1");
//            projectId = null;
//            System.out.println("2");
//            System.out.println("222222");
//        }
//        else {
//            System.out.println("3");
//            projectId = (Long)data;
//            System.out.println("4");
//        }
        System.out.println("5");
        List<Calendar> scheduleList = calendarRepository.findByProjectId(data);
        System.out.println("6");
        if (scheduleList.isEmpty()) {
            return null;
        }

        System.out.println("scheduleList = " + scheduleList);
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

    public boolean updateSelectedEvent(BigInteger id, String title, String description) {
        System.out.println("CalendarService: 아이디로 스케쥴을 특정하고 해당 일정을 수정하는 메소드:::::::::::::::::::::::::::");

        Calendar selectedEvent = calendarRepository.findById(id).orElse(null);

        selectedEvent.setTitle(title);
        selectedEvent.setDescription(description);

        Calendar updateEvent = calendarRepository.save(selectedEvent);

        if(updateEvent == null){
            return false;
        }
        return true;
    }


//    public CalendarDTO updateSelectedEvent(Map<String, Object> data) {
//        // Calendar selectedEvent = CalendarRepository.findById(data.get("id"));
//        // selectedEvent.setTitle(data.get("title").toString());
//    return null;
//    }
}
