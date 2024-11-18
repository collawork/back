package com.collawork.back.repository.project;

import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, ProjectParticipantId> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO project_participants (project_id, user_id, role) VALUES (:projectId, :userId, :role)", nativeQuery = true)
    void addParticipant(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("role") String role);

}

