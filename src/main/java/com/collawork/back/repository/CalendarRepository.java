package com.collawork.back.repository;

import com.collawork.back.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, BigInteger> {

}
