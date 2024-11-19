package com.collawork.back.service;


import com.collawork.back.dto.calendar.CalendarDTO;
import com.collawork.back.dto.calendar.ExtendedProps;
import com.collawork.back.model.Calendar;
import com.collawork.back.repository.CalendarRepository;
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

        Calendar calendar = new Calendar();

        calendar.setDescription(scheduleInfo.getExtendedProps().getDescription());
        calendar.setCreatedAt(scheduleInfo.getExtendedProps().getCreatedAt());
        calendar.setCreatedBy(scheduleInfo.getExtendedProps().getCreatedBy());

        calendar.setTitle(scheduleInfo.getTitle());
        calendar.setStartTime(scheduleInfo.getStart());
        calendar.setEndTime(scheduleInfo.getEnd());
        calendar.setAllDay(scheduleInfo.isAllDay());
        calendar.setProjectId(scheduleInfo.getGroupId());

        if(calendarRepository.save(calendar) != null){
            return Optional.of(calendar);
        }
        return Optional.empty();
    }


    public List<CalendarDTO> eventsByProjectId(Object data) {

        BigInteger projectId;
        List<Calendar> scheduleList;
        CalendarDTO calendarDTO;
        ExtendedProps extendedProps;
        List<CalendarDTO> calendarDTOList = new ArrayList<>();

        System.out.println("data.getClass().getName() = " + data.getClass().getName());
        if (data == "null") projectId = null;
        else projectId = new BigInteger(String.valueOf(data));

        scheduleList = calendarRepository.findByProjectId(projectId);
        if (scheduleList.isEmpty()) return null;

        System.out.println("scheduleList = " + scheduleList);
        for(Calendar schedule : scheduleList){
            calendarDTO = new CalendarDTO();
            extendedProps = new ExtendedProps();

            extendedProps.setCreatedAt(schedule.getCreatedAt());
            extendedProps.setCreatedBy(schedule.getCreatedBy());
            extendedProps.setDescription(schedule.getDescription());

            calendarDTO.setTitle(schedule.getTitle());
            calendarDTO.setStart(schedule.getStartTime());
            calendarDTO.setEnd(schedule.getEndTime());
            calendarDTO.setAllDay(schedule.isAllDay());
            calendarDTO.setGroupId(schedule.getProjectId());
            calendarDTO.setId(schedule.getId());


            calendarDTO.setExtendedProps(extendedProps);
            calendarDTOList.add(calendarDTO);
        }
        return calendarDTOList;

    }
}
