package com.collawork.back.dto.auth;

import com.collawork.back.model.auth.User;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String company;
    private String position;
    private String phone;
    private String fax;
    private String profileImageUrl;

    public UserDTO(User user, String baseUrl) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.company = user.getCompany();
        this.position = user.getPosition();
        this.phone = user.getPhone();
        this.fax = user.getFax();
        this.profileImageUrl = user.getProfileImage() != null
                ? baseUrl + "/uploads/" + user.getProfileImage()
                : baseUrl + "/default-profile.png";
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
