package com.collawork.back.repository;

import com.collawork.back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByOauthIdAndOauthProvider(String oauthId, String oauthProvider);

    User findByUsername(String username);
    User findByPhone(String phone);
}
