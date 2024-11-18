package com.collawork.back.repository.project;

import com.collawork.back.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


//   List<String> findById(Long Id);

    List<Project> findByCreatedBy(Long userId);


//    List<String> findByUserId(Long userId);

     List<Project> findByProjectNameContaining(String query);

    List<Project> findByProjectName(String projectName);
}
