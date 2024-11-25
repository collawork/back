package com.collawork.back.repository.project;

import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, ProjectParticipantId> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO project_participants (project_id, user_id, role) VALUES (:projectId, :userId, :role)", nativeQuery = true)
    void addParticipant(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("role") String role);

    @Query("SELECT p.projectName " +
            "FROM ProjectParticipant pp " +
            "JOIN pp.project p " +
            "WHERE pp.user.id = :userId")
    List<String> findProjectTitlesByUserId(@Param("userId") Long userId);

    @Query("SELECT u.username, u.email FROM ProjectParticipant pp " +
            "JOIN pp.user u " +
            "WHERE pp.project.id IN (" +
            "    SELECT p.project.id FROM ProjectParticipant p WHERE p.user.id = :userId" +
            ")")
    List<Object[]> findParticipantsByUserId(@Param("userId") Long userId);

    @Query("SELECT pp FROM ProjectParticipant pp WHERE pp.project.id = :projectId AND pp.status = 'ACCEPTED'")
    List<ProjectParticipant> findAcceptedParticipantsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pp FROM ProjectParticipant pp WHERE pp.project.id = :projectId")
    List<ProjectParticipant> findAllParticipantsByProjectId(@Param("projectId") Long projectId);

    // 모든 참가자 조회
    @Query("SELECT pp FROM ProjectParticipant pp WHERE pp.project.id = :projectId")
    List<ProjectParticipant> getAllParticipants(@Param("projectId") Long projectId);

    // 승인된 참가자만 조회
    @Query("SELECT pp FROM ProjectParticipant pp WHERE pp.project.id = :projectId AND pp.status = 'ACCEPTED'")
    List<ProjectParticipant> getAcceptedParticipants(@Param("projectId") Long projectId);

    @Query("SELECT p.projectName FROM ProjectParticipant pp " +
            "JOIN pp.project p " +
            "WHERE pp.user.id = :userId AND pp.status = 'ACCEPTED'")
    List<String> findAcceptedProjectsByUserId(@Param("userId") Long userId);

    @Query("SELECT u.username, u.email " +
            "FROM ProjectParticipant pp " +
            "JOIN pp.user u " +
            "WHERE pp.project.id = :projectId AND pp.status = 'PENDING'")
    List<Object[]> findPendingParticipantsByProjectId(@Param("projectId") Long projectId);



    Optional<ProjectParticipant> findByProjectIdAndUserId(Long projectId, Long userId);


    @Query("SELECT pp FROM ProjectParticipant pp WHERE pp.project.id = :projectId AND pp.id.userId IN :userIds")
    List<ProjectParticipant> findByProjectIdAndUserIdIn(@Param("projectId") Long projectId, @Param("userIds") List<Long> userIds);

    /**
     * 특정 프로젝트에서 특정 사용자가 지정된 역할을 가지고 있는지 확인.
     *
     * @param projectId 프로젝트 ID
     * @param userId 사용자 ID
     * @param role 역할 (예: "ADMIN", "MEMBER")
     * @return 역할 여부 확인 결과 (true: 역할 존재, false: 역할 없음)
     */
    boolean existsByProjectIdAndUserIdAndRole(Long projectId, Long userId, ProjectParticipant.Role role);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}

