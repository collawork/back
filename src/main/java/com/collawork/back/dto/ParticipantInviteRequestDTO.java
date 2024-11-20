package com.collawork.back.dto;

import java.util.List;

public class ParticipantInviteRequestDTO {
    private List<Long> participants;

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }
}
