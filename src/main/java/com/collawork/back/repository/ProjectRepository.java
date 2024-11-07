package com.collawork.back.repository;

import com.collawork.back.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


//   List<Project> findByTitle(String title);

    List<Project> findByProjectNameContaining(String query);
}
