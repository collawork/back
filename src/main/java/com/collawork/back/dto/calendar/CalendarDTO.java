package com.collawork.back.dto.calendar;

import java.math.BigInteger;
import java.time.ZonedDateTime;

public class CalendarDTO {

    // private BigInteger id; // 스케쥴의 고유 아이디는 DB에서 부여한다. 그러니 사용자로부터 값을 받을 필요가 없다.
    // private Timestamp createAt; // 스케쥴 생성일을 DB에서 부여한다. 그러니 사용자로부터 값을 받을 필요가 없다.

    private BigInteger id;
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private boolean allDay;
    private BigInteger groupId;
    private boolean editable;
    private String color;

    private ExtendedProps extendedProps;

    public CalendarDTO() {
    }

    public CalendarDTO(BigInteger id, String title, ZonedDateTime start, ZonedDateTime end, boolean allDay, BigInteger groupId, boolean editable, String color, ExtendedProps extendedProps) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.groupId = groupId;
        this.editable = editable;
        this.color = color;
        this.extendedProps = extendedProps;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
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
                ", groupId=" + groupId +
                ", editable=" + editable +
                ", color='" + color + '\'' +
                ", extendedProps=" + extendedProps +
                '}';
    }
}
