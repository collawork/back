package com.collawork.back.service;

import com.collawork.back.model.project.Project;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Voting;
import com.collawork.back.model.project.VotingContents;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.VotingContentsRepository;
import com.collawork.back.repository.project.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private VotingContentsRepository votingContentsRepository;

    // 프로젝트 추가
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

    // id 로 프로젝트 이름 조회
    public List<String> selectProjectTitleByUserId(Long userId) {

        List<Project> titleList = projectRepository.findByCreatedBy(userId);
        System.out.println("ProjectService 의 titleList : " +titleList);
        List<String> listTitle = titleList.stream().map(Project::getProjectName).collect(toList());
        System.out.println("ProjectService 의 listTitle" + listTitle);
        if(titleList .isEmpty()){
            return null;
        }else{
            return listTitle;
        }

    }


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


    public List<Voting> votingInsert(String votingName, String projectId, String createdUser) {

        Voting voting = new Voting();
        voting.setVotingName(votingName);
        voting.setProjectId(Long.valueOf(projectId));
        voting.setCreatedUser(createdUser);
        LocalDate localDate = LocalDate.now();
        voting.setCreatedAt(localDate.atStartOfDay());
        voting.setVote(true);

        List<Voting> result = Collections.singletonList(votingRepository.save(voting));


        return result;


     }

    public boolean insertVoteContents(List<String> contents, Long id) {

        for(String con : contents){
            System.out.println("service 의 contents :: " + con);
        }

        VotingContents votingContents = new VotingContents();
        votingContents.setVotingId(id);
        for (String content : contents) {
            votingContents.setVotingContents(content);
            votingContentsRepository.save(votingContents);
        }
        return true;
    }
}
