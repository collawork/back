package com.collawork.back.model.project;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="board_comment")
public class BoardComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "comment_by")
    private Long commentBy;

    @Column(name = "comment_at")
    private LocalDateTime commentAt;

    @Column(name = "comment_state")
    private Long commentState;

    public BoardComments() {
    }

    public BoardComments(Long id, Long boardId, Long commentBy, LocalDateTime commentAt, Long commentState) {
        this.id = id;
        this.boardId = boardId;
        this.commentBy = commentBy;
        this.commentAt = commentAt;
        this.commentState = commentState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(Long commentBy) {
        this.commentBy = commentBy;
    }

    public LocalDateTime getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(LocalDateTime commentAt) {
        this.commentAt = commentAt;
    }

    public Long getCommentState() {
        return commentState;
    }

    public void setCommentState(Long commentState) {
        this.commentState = commentState;
    }

    @Override
    public String toString() {
        return "BoardComments{" +
                "id=" + id +
                ", boardId=" + boardId +
                ", commentBy=" + commentBy +
                ", commentAt=" + commentAt +
                ", commentState=" + commentState +
                '}';
    }
}
