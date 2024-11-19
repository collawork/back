package com.collawork.back.model.project;

import com.collawork.back.model.auth.User;
import jakarta.persistence.*;

@Entity
@Table(name = "project_participants")
public class ProjectParticipant {

    @EmbeddedId
    private ProjectParticipantId id; // 복합 기본 키를 위한 EmbeddedId

    @ManyToOne
    @MapsId("projectId") // 복합 키의 projectId와 매핑
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @MapsId("userId") // 복합 키의 userId와 매핑
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    public enum Role {
        MEMBER,
        ADMIN
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ProjectParticipant() {}

    public ProjectParticipant(ProjectParticipantId id, Role role) {
        this.id = id;
        this.role = role;
    }

    public ProjectParticipantId getId() {
        return id;
    }

    public void setId(ProjectParticipantId id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
