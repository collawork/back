package com.collawork.back.repository.project;

import com.collawork.back.model.project.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 특정 프로젝트의 공지사항 목록 조회.
     * 중요 여부(important: true)를 우선으로 정렬하고, 최신 생성일(createdAt) 순으로 정렬.
     *
     * @param projectId 프로젝트 ID
     * @return 정렬된 공지사항 목록
     */
    List<Notice> findByProjectIdOrderByImportantDescCreatedAtDesc(Long projectId);

    /**
     * 특정 공지사항이 존재하는지 확인.
     *
     * @param id 공지사항 ID
     * @param projectId 프로젝트 ID
     * @return 공지사항 존재 여부
     */
    boolean existsByIdAndProjectId(Long id, Long projectId);

    List<Notice> findTop3ByProjectIdAndImportantOrderByCreatedAtDesc(Long projectId, boolean b);
}
