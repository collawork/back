package com.collawork.back.repository.project;

import com.collawork.back.model.project.Project;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


//   List<String> findById(Long Id);

    List<Project> findByCreatedBy(Long userId);


//    List<String> findByUserId(Long userId);

     List<Project> findByProjectNameContaining(String query);

    List<Project> findByProjectName(String projectName);

    @Query("SELECT p FROM Project p JOIN ProjectParticipant pp ON p.id = pp.project.id " +
            "WHERE pp.user.id = :userId AND p.projectName LIKE %:projectName%")
    List<Project> findByProjectNameAndUserId(@Param("projectName") String projectName, @Param("userId") Long userId);

    @Query("SELECT p.projectName FROM Project p WHERE p.createdBy = :userId")
    List<String> findProjectTitlesByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Project p JOIN ProjectParticipant pp ON p.id = pp.project.id " +
            "WHERE pp.user.id = :userId AND pp.status = 'ACCEPTED'")
    List<Project> findAcceptedProjectsByUserId(@Param("userId") Long userId);


}
