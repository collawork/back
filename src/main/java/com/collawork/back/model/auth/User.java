package com.collawork.back.model.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String oauthProvider;
    private String oauthId;
    private String profileImage;
    private String company;
    private String position;
    private String phone;
    private String fax;
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {
    }

    public User(Long id, String username, String email, String password, String oauthProvider, String oauthId, String profileImage, String company, String position, String phone, String fax, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.profileImage = profileImage;
        this.company = company;
        this.position = position;
        this.phone = phone;
        this.fax = fax;
        this.createdAt = createdAt;
    }

    public User(Long userId) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", oauthProvider='" + oauthProvider + '\'' +
                ", oauthId='" + oauthId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
