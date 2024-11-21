package com.collawork.back.model.project;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "project_board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "board_title")
    private String boardTitle;

    @Column(name = "board_contents")
    private String boardContents;

    @Column(name = "board_by")
    private Long boardBy;

    @Column(name = "board_at")
    private LocalDateTime boardAt;

    public Board() {
    }

    public Board(Long id, Long projectId, String boardTitle, String boardContents, Long boardBy, LocalDateTime boardAt) {
        this.id = id;
        this.projectId = projectId;
        this.boardTitle = boardTitle;
        this.boardContents = boardContents;
        this.boardBy = boardBy;
        this.boardAt = boardAt;
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

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getBoardContents() {
        return boardContents;
    }

    public void setBoardContents(String boardContents) {
        this.boardContents = boardContents;
    }

    public Long getBoardBy() {
        return boardBy;
    }

    public void setBoardBy(Long boardBy) {
        this.boardBy = boardBy;
    }

    public LocalDateTime getBoardAt() {
        return boardAt;
    }

    public void setBoardAt(LocalDateTime boardAt) {
        this.boardAt = boardAt;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", boardTitle='" + boardTitle + '\'' +
                ", boardContents='" + boardContents + '\'' +
                ", boardBy=" + boardBy +
                ", boardAt=" + boardAt +
                '}';
    }
}
