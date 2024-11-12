package com.collawork.back.service;


import com.collawork.back.dto.CalendarDTO;
import com.collawork.back.model.Calendar;
import com.collawork.back.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Optional<Calendar> insertSchedule(CalendarDTO scheduleInfo) {

        Calendar calendar = new Calendar();

        calendar.setProjectId(scheduleInfo.getProjectId()); // 있으면 받고
        calendar.setTitle(scheduleInfo.getTitle());
        calendar.setDescription(scheduleInfo.getDescription()); // 있으면 받고
        calendar.setStartTime(scheduleInfo.getStart());
        calendar.setEndTime(scheduleInfo.getEnd()); // 있으면 받고
        calendar.setCreateBy(scheduleInfo.getCreateBy());

        if(calendarRepository.save(calendar) != null){
            return Optional.of(calendar);
        }
        return Optional.empty();
    }
}
