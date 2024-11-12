package com.collawork.back.model;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "calendar_events")
public class Calendar {

    //일정의 고유 ID, DB에서 자동 부여
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    // 프로젝트 아이디가 존재하는 스케쥴은 프로젝트 일정. 존재하지 않으면 개인 일정.
    @Column(name = "project_id")
    private BigInteger projectId;

    // 일정의 제목, not null
    @Column(name = "title")
    private String title;

    // 일정의 상세 설명
    @Column(name = "description")
    private String description;

    // 일정의 시작일, not null
    @Column(name = "start_time")
    private LocalDateTime startTime;

    // 일정의 종료일, 없으면 일정의 시작일만 존재하도록..
    @Column(name = "end_time")
    private LocalDateTime endTime;

    // 일정을 등록한 사람, not null
    @Column(name = "create_by")
    private BigInteger createBy;

    // 일정이 추가된 날짜, not null
    @Column(name = "create_at")
    private LocalDateTime createAt;

    public Calendar() {
    }

    public Calendar(BigInteger id, BigInteger projectId, String title, String description, LocalDateTime startTime, LocalDateTime endTime, BigInteger createBy, LocalDateTime createAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createBy = createBy;
        this.createAt = createAt;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getProjectId() {
        return projectId;
    }

    public void setProjectId(BigInteger projectId) {
        this.projectId = projectId;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigInteger getCreateBy() {
        return createBy;
    }

    public void setCreateBy(BigInteger createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createBy=" + createBy +
                ", createAt=" + createAt +
                '}';
    }
}
