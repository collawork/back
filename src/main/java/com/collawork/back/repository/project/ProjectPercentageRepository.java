package com.collawork.back.repository.project;

import com.collawork.back.model.project.ProjectPercentage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectPercentageRepository extends JpaRepository<ProjectPercentage,Long> {
    ProjectPercentage findByProjectId(Long projectId);
}
