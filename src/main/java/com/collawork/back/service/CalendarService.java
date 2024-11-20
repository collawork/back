package com.collawork.back.service;


import com.collawork.back.dto.CalendarDTO;
import com.collawork.back.model.Calendar;
import com.collawork.back.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Optional<Calendar> insertSchedule(CalendarDTO scheduleInfo) {

        Calendar calendar = new Calendar();

        calendar.setTitle(scheduleInfo.getTitle());
        calendar.setDescription(scheduleInfo.getDescription());
        calendar.setStartTime(scheduleInfo.getStart());
        calendar.setEndTime(scheduleInfo.getEnd());
        calendar.setCreatedAt(scheduleInfo.getCreatedAt());
        calendar.setCreatedBy(scheduleInfo.getCreatedBy());
        calendar.setAllDay(scheduleInfo.isAllDay());
        calendar.setProjectId(scheduleInfo.getProjectId());

        if(calendarRepository.save(calendar) != null){
            return Optional.of(calendar);
        }
        return Optional.empty();
    }


    public Optional<Calendar> eventsByProjectId(Object data) {
        Calendar calendar = new Calendar();
        if (data == "null") calendar.setProjectId(null);
        else calendar.setProjectId((BigInteger) data);
        if(calendarRepository.save(calendar) != null){
            return Optional.of(calendar);
        }
        return Optional.empty();
    }
}
