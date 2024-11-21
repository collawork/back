package com.collawork.back.dto.calendar;

import java.math.BigInteger;
import java.time.ZonedDateTime;

public class ExtendedProps {

     private String description;
     private ZonedDateTime createdAt;
     private Long createdBy;
     private Long projectId;

     public ExtendedProps() {
     }

    public ExtendedProps(String description, ZonedDateTime createdAt, Long createdBy, Long projectId) {
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "ExtendedProps{" +
                "description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", projectId=" + projectId +
                '}';
    }
}
