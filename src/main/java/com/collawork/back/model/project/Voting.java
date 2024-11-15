package com.collawork.back.model.project;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "voting")
public class Voting {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voting_name")
    private String votingName;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "created_user")
    private String createdUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_vote")
    private Boolean isVote;

    public Voting() {
    }

    public Voting(Long id, String votingName, Long projectId, String createdUser, LocalDateTime createdAt, Boolean isVote) {
        this.id = id;
        this.votingName = votingName;
        this.projectId = projectId;
        this.createdUser = createdUser;
        this.createdAt = createdAt;
        this.isVote = isVote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVotingName() {
        return votingName;
    }

    public void setVotingName(String votingName) {
        this.votingName = votingName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getVote() {
        return isVote;
    }

    public void setVote(Boolean vote) {
        isVote = vote;
    }

    @Override
    public String toString() {
        return "Voting{" +
                "id=" + id +
                ", votingName='" + votingName + '\'' +
                ", projectId=" + projectId +
                ", createdUser='" + createdUser + '\'' +
                ", createdAt=" + createdAt +
                ", isVote=" + isVote +
                '}';
    }
}
