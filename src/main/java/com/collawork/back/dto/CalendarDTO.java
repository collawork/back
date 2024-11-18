package com.collawork.back.dto;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CalendarDTO {

    // private BigInteger id; // 스케쥴의 고유 아이디는 DB에서 부여한다. 그러니 사용자로부터 값을 받을 필요가 없다.
    // private Timestamp createAt; // 스케쥴 생성일을 DB에서 부여한다. 그러니 사용자로부터 값을 받을 필요가 없다.

    private BigInteger id;
    private String title;
    private String description;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime createdAt;
    private BigInteger createdBy; //
    private boolean allDay;
    private BigInteger projectId;

    public CalendarDTO() {
    }

    public CalendarDTO(BigInteger id, String title, String description, ZonedDateTime start, ZonedDateTime end, ZonedDateTime createdAt, BigInteger createdBy, boolean allDay, BigInteger projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.allDay = allDay;
        this.projectId = projectId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public BigInteger getProjectId() {
        return projectId;
    }

    public void setProjectId(BigInteger projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "CalendarDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", allDay=" + allDay +
                ", projectId=" + projectId +
                '}';
    }
}
