package com.collawork.back.repository.friends;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.friends.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);

}
