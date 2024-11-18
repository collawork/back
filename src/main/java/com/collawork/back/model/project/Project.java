package com.collawork.back.model.project;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 프로젝트 고유 아이디

    @Column(name = "project_name", nullable = false)
    private String projectName; // 프로젝트 이름

    @Column(name = "created_by")
    private Long createdBy; // 생성자 아이디


    @Column(name = "project_code")
    private String projectCode; // 프로젝트 설명

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt; // 생성일


    public Project() {
    }

    public Project(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectParticipant> participants = new ArrayList<>();


    public Project(Long id, String projectName, Long createdBy, String projectCode, LocalDateTime createdAt) {
        this.id = id;
        this.projectName = projectName;
        this.createdBy = createdBy;
        this.projectCode = projectCode;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", createdBy=" + createdBy +
                ", projectCode='" + projectCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
