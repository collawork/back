package com.collawork.back.model.friends;

import com.collawork.back.model.auth.User;
import com.collawork.back.utils.StatusConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_requester_id"))
    private User requester;

    @ManyToOne
    @JoinColumn(name = "responder_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_responder_id"))
    private User responder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING'")
    private Status status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = Status.PENDING;
        }
    }

    @Convert(converter = StatusConverter.class)
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }


    public Friend() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getResponder() {
        return responder;
    }

    public void setResponder(User responder) {
        this.responder = responder;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
