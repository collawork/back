package com.collawork.back.dto;


import java.time.LocalDate;
import java.util.Date;

public class ProjectDTO {

    private Long projectId;  // 프로젝트 id
    private String projectName; // 프로젝트 네임
    private Long userId; // 유저 아이디
    private Long project_code; // 프로젝트 설명
    private LocalDate createsAt; // 현재 시각

    public ProjectDTO() {
    }

    public ProjectDTO(Long projectId, String projectName, Long userId, Long project_code, LocalDate createsAt) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.userId = userId;
        this.project_code = project_code;
        this.createsAt = createsAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProject_code() {
        return project_code;
    }

    public void setProject_code(Long project_code) {
        this.project_code = project_code;
    }

    public LocalDate getCreatesAt() {
        return createsAt;
    }

    public void setCreatesAt(LocalDate createsAt) {
        this.createsAt = createsAt;
    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", userId=" + userId +
                ", project_code=" + project_code +
                ", createsAt=" + createsAt +
                '}';
    }
}
