package com.collawork.back.repository;

import com.collawork.back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByOauthIdAndOauthProvider(String oauthId, String oauthProvider);
    List<User> findByUsernameContaining(String username);
    User findByUsername(String username);
    User findByPhone(String phone);

    Optional<User> findById(Long id);

}
