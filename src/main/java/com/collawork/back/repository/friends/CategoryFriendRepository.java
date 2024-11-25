package com.collawork.back.repository.friends;

import com.collawork.back.model.friends.CategoryFriend;
import com.collawork.back.model.friends.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryFriendRepository extends JpaRepository<CategoryFriend, Long> {
    List<CategoryFriend> findByCategoryId(Long categoryId);

    List<CategoryFriend> findByCategoryIdAndFriendIdIn(Long categoryId, java.util.List<java.lang.Long> friendIds);
}
