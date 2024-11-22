package com.collawork.back.model.calendar;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "calendar_events")
public class Calendar {

    //일정의 고유 ID, DB에서 자동 부여
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일정의 제목, not null
    @Column(name = "title")
    private String title;

    // 일정의 상세 설명
    @Column(name = "description")
    private String description;

    // 일정의 시작일, not null
    @Column(name = "start_time")
    private ZonedDateTime startTime;

    // 일정의 종료일, 없으면 일정의 시작일만 존재하도록..
    @Column(name = "end_time")
    private ZonedDateTime endTime;

    // 일정이 추가된 날짜, not null
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    // 일정을 등록한 사람, not null
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "all_day")
    private boolean allDay;

    @Column(name = "project_id")
    private Long projectId;

    //@Column(name = "editable")
    //private boolean editable;

    @Column(name = "color")
    private String color;

    public Calendar() {
    }

    public Calendar(Long id, String title, String description, ZonedDateTime startTime, ZonedDateTime endTime, ZonedDateTime createdAt, Long createdBy, boolean allDay, Long projectId, String color) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.allDay = allDay;
        this.projectId = projectId;
        this.color = color;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", allDay=" + allDay +
                ", projectId=" + projectId +
                ", color='" + color + '\'' +
                '}';
    }
}
