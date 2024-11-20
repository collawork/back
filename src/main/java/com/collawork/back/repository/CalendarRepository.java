package com.collawork.back.repository;

import com.collawork.back.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, BigInteger> {

    List<Calendar> findByProjectId(BigInteger projectId);
}
