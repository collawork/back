package com.collawork.back.service;

import com.collawork.back.dto.MessageDTO;
import com.collawork.back.dto.ProjectDTO;
import com.collawork.back.model.Project;
import com.collawork.back.model.User;
import com.collawork.back.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public void insertProject(String title, String context) {

        User user = new User();
        Project project = new Project();

        project.setProjectName(title); // 프로젝트 이름
        Long createdBy = user.getId();
        project.setId(createdBy); // 생성자 id
        project.setProjectCode(context); // 프로젝트 설명
        LocalDate localDate = LocalDate.now();
        project.setCreatedAt(localDate.atStartOfDay());

         // 프로젝트 생성일
        projectRepository.save(project);

    }
}
