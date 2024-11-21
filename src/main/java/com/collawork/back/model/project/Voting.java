package com.collawork.back.model.project;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "voting")
public class Voting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voting_name",nullable = false)
    private String votingName;

    @Column(name = "voting_detail")
    private String votingDetail;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "created_user")
    private String createdUser;

    @Column(name = "voting_end") // 지정한 마감일
    private LocalDateTime votingEnd;

    @Column(name = "user_voted") // 유저의 투표 유무
    private Boolean userVoted;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일

    @Column(name = "is_vote", columnDefinition = "TIMESTAMP")
    private Boolean isVote;

    public Voting() {
    }

    public Voting(Long id, String votingName, String votingDetail, Long projectId, String createdUser, LocalDateTime votingEnd, Boolean userVoted, LocalDateTime createdAt, Boolean isVote) {
        this.id = id;
        this.votingName = votingName;
        this.votingDetail = votingDetail;
        this.projectId = projectId;
        this.createdUser = createdUser;
        this.votingEnd = votingEnd;
        this.userVoted = userVoted;
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

    public String getVotingDetail() {
        return votingDetail;
    }

    public void setVotingDetail(String votingDetail) {
        this.votingDetail = votingDetail;
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

    public LocalDateTime getVotingEnd() {
        return votingEnd;
    }

    public void setVotingEnd(LocalDateTime votingEnd) {
        this.votingEnd = votingEnd;
    }

    public Boolean getUserVoted() {
        return userVoted;
    }

    public void setUserVoted(Boolean userVoted) {
        this.userVoted = userVoted;
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
                ", votingDetail='" + votingDetail + '\'' +
                ", projectId=" + projectId +
                ", createdUser='" + createdUser + '\'' +
                ", votingEnd=" + votingEnd +
                ", userVoted=" + userVoted +
                ", createdAt=" + createdAt +
                ", isVote=" + isVote +
                '}';
    }
}
