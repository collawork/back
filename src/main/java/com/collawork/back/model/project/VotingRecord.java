package com.collawork.back.model.project;


import jakarta.persistence.*;

@Entity
@Table(name = "voting_record")
public class VotingRecord {

    public VotingRecord() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voting_id")
    private Long votingId;

    @Column(name = "contents_id")
    private Long contentsId;

    @Column(name = "user_id")
    private Long userId;

    public VotingRecord(Long id, Long votingId, Long contentsId, Long userId) {
        this.id = id;
        this.votingId = votingId;
        this.contentsId = contentsId;
        this.userId = userId;
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

    public Long getContentsId() {
        return contentsId;
    }

    public void setContentsId(Long contentsId) {
        this.contentsId = contentsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "VotingRecord{" +
                "id=" + id +
                ", votingId=" + votingId +
                ", contentsId=" + contentsId +
                ", userId=" + userId +
                '}';
    }
}
