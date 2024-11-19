package com.collawork.back.dto.calendar;

import java.math.BigInteger;
import java.time.ZonedDateTime;

public class ExtendedProps {

     private String description;
     private ZonedDateTime createdAt;
     private BigInteger createdBy;

    public ExtendedProps() {
    }

    public ExtendedProps(String description, ZonedDateTime createdAt, BigInteger createdBy) {
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
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

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "ExtendedProps{" +
                "description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                '}';
    }
}
