package com.collawork.back.service;

import com.collawork.back.model.project.Project;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.Voting;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectParticipantRepository projectParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Long insertProject(String title, String context, Long userId) {
        Project project = new Project();
        project.setProjectName(title);
        project.setCreatedBy(userId);
        project.setProjectCode(context);
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        // 프로젝트 생성자를 ADMIN 역할로 project_participants에 추가
        ProjectParticipant creator = new ProjectParticipant();
        creator.setProject(savedProject);
        creator.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        creator.setRole(ProjectParticipant.Role.ADMIN);
        projectParticipantRepository.save(creator);

        return savedProject.getId();
    }


    // id 로 프로젝트 이름 조회
//    public List<String> selectProjectTitleByUserId(Long userId) {
//
//        List<Project> titleList = projectRepository.findByCreatedBy(userId);
//        System.out.println("ProjectService 의 titleList : " +titleList);
//        List<String> listTitle = titleList.stream().map(Project::getProjectName).collect(toList());
//        System.out.println("ProjectService 의 listTitle" + listTitle);
//        if(titleList .isEmpty()){
//            return Collections.emptyList(); // 빈 리스트 반환
//        }else{
//            return listTitle;
//        }
//
//    }

    public List<String> selectProjectTitleByUserId(Long userId) {
        return projectRepository.findProjectTitlesByUserId(userId);
    }


//    // 프로젝트 정보 조회
//    public List<String> selectProjectByUserId(Long userId) {
//
//        List<Project> projectList = projectRepository.findByCreatedBy(userId);
//        System.out.println("ProjectService 의 projectList : " + projectList);
//
//        return projectList



    // id 로 유저 정보 조회(관리자)
    public Optional<User> selectUserNameByUserId(Long id) {

        Optional<User> userList = userRepository.findById(id);
        System.out.println("ProjectService 의 유저 정보 조회 : " + userList);
        return userList;

}

    // ProjectName 으로 프로젝트 정보 조회
    public List<Project> selectByProjectName(String projectName) {

        List<Project> titleList = projectRepository.findByProjectName(projectName);
        System.out.println("ProjectService 의 selectByProjectName : " +titleList);
        return titleList;

    }

    public boolean votingInsert(String votingName, String projectId, String createdUser) {

        Voting voting = new Voting();
        voting.setVotingName(votingName);
        voting.setProjectId(Long.valueOf(projectId));
        voting.setCreatedUser(createdUser);
        LocalDate localDate = LocalDate.now();
        voting.setCreatedAt(localDate.atStartOfDay());

//        if(projectRepository.save(voting) != null){
//            return true;
//        }else{
//            return false;
//        }
        return false;

     }
}
