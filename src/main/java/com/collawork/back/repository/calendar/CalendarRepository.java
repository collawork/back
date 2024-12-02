package com.collawork.back.repository.calendar;

import com.collawork.back.model.calendar.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {



    List<Calendar> findByProjectId(Long projectId);

    List<Calendar> findByCreatedBy(Long userId);
}
