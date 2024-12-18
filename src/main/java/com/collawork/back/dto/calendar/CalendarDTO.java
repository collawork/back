package com.collawork.back.dto.calendar;

import java.math.BigInteger;
import java.time.ZonedDateTime;

public class CalendarDTO {

    private Long id;
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private boolean allDay;
    private String color;
    private ExtendedProps extendedProps;


    public CalendarDTO() {
    }

    public CalendarDTO(Long id, String title, ZonedDateTime start, ZonedDateTime end, boolean allDay, String color, ExtendedProps extendedProps) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.color = color;
        this.extendedProps = extendedProps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ExtendedProps getExtendedProps() {
        return extendedProps;
    }

    public void setExtendedProps(ExtendedProps extendedProps) {
        this.extendedProps = extendedProps;
    }

    @Override
    public String toString() {
        return "CalendarDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", allDay=" + allDay +
                ", color='" + color + '\'' +
                ", extendedProps=" + extendedProps +
                '}';
    }
}
