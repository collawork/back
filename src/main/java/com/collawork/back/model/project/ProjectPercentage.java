package com.collawork.back.model.project;


import jakarta.persistence.*;

@Entity
@Table(name = "project_percentage")
public class ProjectPercentage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "percent")
    private Long percent;

    public ProjectPercentage() {
    }

    public ProjectPercentage(Long id, Long projectId, Long percent) {
        this.id = id;
        this.projectId = projectId;
        this.percent = percent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPercent() {
        return percent;
    }

    public void setPercent(Long percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "ProjectPercentage{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", percent=" + percent +
                '}';
    }
}
