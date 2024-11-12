package com.collawork.back.dto;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarDTO {

    // private BigInteger id; // 스케쥴의 고유 아이디는 DB에서 부여한다. 그러니 사용자로부터 값을 받을 필요가 없다.
    // private Timestamp createAt; // 스케쥴 생성일을 DB에서 부여한다. 그러니 사용자로부터 값을 받을 필요가 없다.

    private String title;
    private LocalDateTime start;
    private LocalDateTime end;

    private BigInteger projectId; //
    private String description; //
    private BigInteger createBy; //

    // private Map<String, String> extendedProps = {projectId, description, createBy};


    public CalendarDTO() {
    }

    public CalendarDTO(String title, LocalDateTime start, LocalDateTime end, BigInteger projectId, String description, BigInteger createBy) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.projectId = projectId;
        this.description = description;
        this.createBy = createBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public BigInteger getProjectId() {
        return projectId;
    }

    public void setProjectId(BigInteger projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getCreateBy() {
        return createBy;
    }

    public void setCreateBy(BigInteger createBy) {
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return "CalendarDTO{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", projectId=" + projectId +
                ", description='" + description + '\'' +
                ", createBy=" + createBy +
                '}';
    }
}
