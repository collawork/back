package com.collawork.back.model.friends;

import com.collawork.back.model.auth.User;
import jakarta.persistence.*;

@Entity
@Table(name = "category_friends")
public class CategoryFriend {

        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 연관관계 고유 키값

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 카테고리와의 연관관계

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend; // 친구와의 연관관계

    public CategoryFriend(Category category, User friend) {
    }

    public CategoryFriend() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
