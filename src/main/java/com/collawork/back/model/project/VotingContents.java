package com.collawork.back.model.project;


import jakarta.persistence.*;

@Entity // 투표 항목 정보를 저장
@Table(name = "voting_contents")
public class VotingContents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voting_id")
    private Long votingId;

    @Column(name = "voting_contents")
    private String votingContents;

    public VotingContents() {
    }

    public VotingContents(Long votingId, String votingContents) {
        this.votingId = votingId;
        this.votingContents = votingContents;
    }

    public VotingContents(Long id, Long votingId, String votingContents) {
        this.id = id;
        this.votingId = votingId;
        this.votingContents = votingContents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVotingId() {
        return votingId;
    }

    public void setVotingId(Long votingId) {
        this.votingId = votingId;
    }

    public String getVotingContents() {
        return votingContents;
    }

    public void setVotingContents(String votingContents) {
        this.votingContents = votingContents;
    }

    @Override
    public String toString() {
        return "VotingParticipants{" +
                "id=" + id +
                ", votingId=" + votingId +
                ", votingContents='" + votingContents + '\'' +
                '}';
    }
}
