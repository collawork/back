package com.collawork.back.repository.project;


import com.collawork.back.model.project.BoardComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComments, Long> {
}
