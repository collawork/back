package com.collawork.back.model.project;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class NoticeRequest {
    private String title;
    private String content;
    private boolean important;
    private List<MultipartFile> attachments; // 여러 파일 업로드

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }
}
