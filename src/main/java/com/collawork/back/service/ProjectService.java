package com.collawork.back.service;

import com.collawork.back.dto.MessageDTO;
import com.collawork.back.dto.ProjectDTO;
import com.collawork.back.model.Project;
import com.collawork.back.model.User;
import com.collawork.back.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Boolean insertProject(String title, String context, Long userId) {


        Project project = new Project();

        project.setProjectName(title); // 프로젝트 이름
        project.setCreatedBy(userId);
        System.out.println(userId);
        project.setProjectCode(context); // 프로젝트 설명
        System.out.println("ProjectService: " +context);
        LocalDate localDate = LocalDate.now();
        project.setCreatedAt(localDate.atStartOfDay());// 프로젝트 생성일

        if(projectRepository.save(project) != null){
            return true;
        }else{
            return false;
        }

    }

//    public List<String> selectProjectName( String userId) {
//
//        List<String> titleList = projectRepository.findByTitle(userId);
//
//        if(titleList.size() > 0){
//            return titleList;
//        }
//        return null;
//    }
}
