package com.collawork.back.model.project;

import com.collawork.back.service.ProjectService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VotingScheduler {

    private final ProjectService projectService;

    public VotingScheduler(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndUpdateExpiredVotings() {
        projectService.updateExpiredVotings();
    }
}