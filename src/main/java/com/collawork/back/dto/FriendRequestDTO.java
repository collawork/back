package com.collawork.back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FriendRequestDTO {
    @JsonProperty("requesterId")
    private Long requesterId;

    @JsonProperty("responderId")
    private Long responderId;

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public Long getResponderId() {
        return responderId;
    }

    public void setResponderId(Long responderId) {
        this.responderId = responderId;
    }
}
