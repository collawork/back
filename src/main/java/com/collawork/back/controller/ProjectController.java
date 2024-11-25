package com.collawork.back.controller;

import com.collawork.back.model.ChatRooms;
import com.collawork.back.model.calendar.Calendar;
import com.collawork.back.model.project.*;
import com.collawork.back.model.auth.User;
import com.collawork.back.repository.ChatRoomRepository;
import com.collawork.back.dto.ParticipantInviteRequestDTO;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.calendar.CalendarRepository;
 import com.collawork.back.repository.project.NoticeRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.repository.project.VotingRecordRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectParticipantsService;
import com.collawork.back.service.ProjectService;
import com.collawork.back.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private VotingRecordRepository votingRecordRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private ProjectParticipantsService projectParticipantsService;

    @Autowired
    private ProjectParticipantRepository projectParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);


    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectInfo(@PathVariable Long projectId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        Project project = projectRepository.findById(projectId).orElse(null);

        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(project);
    }

    @PostMapping("/newproject")
    public ResponseEntity<String> newProject(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {

        log.debug("Received request data: {}", requestData);


        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        try {
            // 요청 데이터 파싱
            String title = (String) requestData.get("title");
            String context = (String) requestData.get("context");
            Long userId = Long.valueOf(requestData.get("userId").toString());
            List<Long> participants = ((List<?>) requestData.get("participants")).stream()
                    .map(participant -> Long.valueOf(participant.toString()))
                    .toList();

            User user = new User();

            log.debug("Project ID created: {}", userId);

            // 프로젝트 생생 시 프로젝트 채팅방 생성
            ChatRooms chatRoom = new ChatRooms();
            chatRoom.setRoomName(title); // 채팅방 이름(=프로젝트 이름)
            chatRoom.setCreatedBy(user.setId(userId)); // 만든사람
            chatRoom.setCreatedAt(LocalDateTime.now()); // 생성 시간
            List<ChatRooms> chatRoomm = Collections.singletonList(chatRoomRepository.save(chatRoom));

            Long chatRoomId = chatRoomm.get(0).getId();


            // 프로젝트 생성
            Long projectId = projectService.insertProject(title, context, userId, participants, chatRoomId);

            // ADMIN 역할로 생성자 추가
            projectParticipantsService.addParticipant(projectId, userId, ProjectParticipant.Role.ADMIN);

            // MEMBER 역할로 참가자 추가
            for (Long participantId : participants) {
                projectParticipantsService.addParticipant(projectId, participantId, ProjectParticipant.Role.MEMBER);
            }

            return ResponseEntity.ok("프로젝트가 생성되었습니다.");
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body("요청 데이터 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/selectAll")
    public ResponseEntity<Object> getProjectTitle(@RequestBody Map<String, Object> requestBody,
                                                  HttpServletRequest request) {
        System.out.println("요청 바디: " + requestBody);

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        Long userId;
        try {
            userId = Long.valueOf(requestBody.get("userId").toString());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("userId 형식이 잘못되었습니다.");
        }

        // 프로젝트 ID와 이름을 모두 조회
        List<Map<String, Object>> projectList = projectService.selectAcceptedProjectsByUserId(userId);

        if (projectList == null || projectList.isEmpty()) {
            return ResponseEntity.ok("생성한 프로젝트가 없습니다.");
        }

        return ResponseEntity.ok(projectList);
    }


    @PostMapping("/projecthomeusers") // 유저 정보 조회
    public ResponseEntity<Object> getProjectHome(@RequestParam("id") Long userId, HttpServletRequest request) {

        System.out.println("projectHome 의 userId : " + userId);

        String token = request.getHeader("Authorization");


        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }


        // 프로젝트 생성자의 정보 조회
        Optional<User> users = projectService.selectUserNameByUserId(Long.valueOf(userId));


        if (users.isEmpty()) {
            return ResponseEntity.ok("프로젝트 생성자의 정보가 없습니다.");
        }
        return ResponseEntity.ok(users); // 프로젝트 정보 리스트.
    }

    // 프로젝트 전체 조회 메소드
    @PostMapping("projectselect")
    public ResponseEntity<Object> getProjectSelect(@RequestParam("projectName") String projectName,
                                                   HttpServletRequest request) {



        String token = request.getHeader("Authorization");



        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }


        // projectName 으로 project 엔티티 조회 후 가져옴
        List<Project> projectList = projectService.selectByProjectName(projectName);

        if (projectList.isEmpty()) {
            return ResponseEntity.status(403).body("조회된 정보가 없습니다.");
        }
        return ResponseEntity.ok(projectList);
    }

    /**
     * 프로젝트 참여자 조회 메소드
     * parmas - userId
     */
    @PostMapping("/participants")
    public ResponseEntity<List<Map<String, Object>>> getProjectParticipants(@RequestBody Map<String, Object> requestBody,
                                                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        Long userId = Long.valueOf(requestBody.get("userId").toString());

        // 참여자 목록 조회
        List<Map<String, Object>> participants = projectService.getParticipantsByUserId(userId);

        return ResponseEntity.ok(participants);
    }

    @PostMapping("/{projectId}/accept")
    public ResponseEntity<String> acceptInvitation(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        projectService.acceptInvitation(projectId, userId);
        return ResponseEntity.ok("프로젝트 초대 승인");
    }

    @PostMapping("/{projectId}/reject")
    public ResponseEntity<String> rejectInvitation(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        projectService.rejectInvitation(projectId, userId);
        return ResponseEntity.ok("프로젝트 초대 거절");
    }

    /**
     * 프로젝트 모든 참가자 조회
     */
    @GetMapping("/{projectId}/participants")
    public ResponseEntity<List<ProjectParticipant>> getAllParticipants(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getAllParticipants(projectId));
    }

    /**
     * 프로젝트 승인한 참가자만 조회
     */
    @PostMapping("/{projectId}/participants/{userId}/accept")
    public ResponseEntity<String> acceptParticipant(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        log.debug("초대 승인 요청: projectId={}, userId={}", projectId, userId);
        projectService.acceptInvitation(projectId, userId);
        return ResponseEntity.ok("참가 요청이 승인되었습니다.");
    }

    /**
     * 프로젝트 승인된 참가자만 조회(userId 안받고)
     */
    @GetMapping("/{projectId}/participants/accepted")
    public ResponseEntity<List<Map<String, Object>>> getAcceptedParticipants(@PathVariable Long projectId) {
        List<ProjectParticipant> participants = projectService.getAcceptedParticipants(projectId);

        System.out.println("프로젝트 참여자 조회 시 projectId : " + projectId);

        // 사용자 정보 포함 여부 확인
        List<Map<String, Object>> formattedParticipants = participants.stream()
                .map(participant -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", participant.getUser().getId());
                    map.put("username", participant.getUser().getUsername());
                    map.put("email", participant.getUser().getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(formattedParticipants);
    }

    /**
     * 프로젝트에 초대된 사용자 조회(승인, 거절 전)
     */
    @GetMapping("/{projectId}/participants/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingParticipants(
            @PathVariable Long projectId,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        // PENDING 상태인 사람만 조회
        List<Map<String, Object>> pendingParticipants = projectService.getPendingParticipants(projectId);

        if (pendingParticipants.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(pendingParticipants);
    }

    @GetMapping("/{projectId}/role")
    public ResponseEntity<Map<String, String>> getUserRole(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        ProjectParticipant participant = projectParticipantRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("참가자 정보를 찾을 수 없습니다."));

        Map<String, String> response = new HashMap<>();
        response.put("role", participant.getRole().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * 프로젝트 생성 후 참여자 초대시 처리하는 로직
     *
     * @param projectId 기대 결과값 : 프로젝트 고유 키
     */
    @PostMapping("/{projectId}/participants/invite")
    public ResponseEntity<?> inviteParticipants(
            @PathVariable Long projectId,
            @RequestBody ParticipantInviteRequestDTO inviteRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 현재 사용자 확인 (권한 체크)
        String currentEmail = userDetails.getUsername();
        boolean isAdmin = projectParticipantsService.isUserAdmin(projectId, currentEmail);

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("프로젝트에서 사용자 초대 권한이 없습니다.");
        }

        // 요청 데이터에서 participants 뽑아오기
        List<Long> participantIds = inviteRequest.getParticipants();
        if (participantIds == null || participantIds.isEmpty()) {
            return ResponseEntity.badRequest().body("참가자 ID가 제공되지 않았습니다.");
        }

        // Null 값 필터링
        participantIds = participantIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (participantIds.isEmpty()) {
            return ResponseEntity.badRequest().body("유효한 참가자 ID가 없습니다.");
        }

        try {
            // 이미 참여 중인 사용자 확인
            List<Long> alreadyAccepted = projectParticipantsService.getAcceptedParticipantsIds(projectId, participantIds);
            if (!alreadyAccepted.isEmpty()) {
                // 사용자 정보 가져오기
                List<String> alreadyAcceptedUserDetails = userRepository.findAllById(alreadyAccepted).stream()
                        .map(user -> user.getUsername() + " (" + user.getEmail() + ")")
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body("이미 참여 중인 사용자: " + alreadyAcceptedUserDetails);
            }

            // REJECTED 상태 사용자도 처리
            List<Long> rejectedUsers = projectParticipantsService.updateRejectedParticipantsToPending(projectId, participantIds);
            if (!rejectedUsers.isEmpty()) {
                return ResponseEntity.ok("거절된 사용자의 상태를 '초대됨(PENDING)'으로 업데이트했습니다: " + rejectedUsers);
            }

            // 초대 처리
            projectParticipantsService.inviteParticipants(projectId, participantIds);

            // **알림 처리 (NotificationService 사용)**
            String projectName = projectService.getProjectNameById(projectId); // 프로젝트 이름 가져오기
            for (Long participantId : participantIds) {
                String message = "프로젝트 '" + projectName + "'에 초대되었습니다.";
                notificationService.createOrUpdateNotification(participantId, projectId, message);
            }

            return ResponseEntity.ok("프로젝트 참가자 초대 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("프로젝트 참가자 초대 중 오류 발생 : " + e.getMessage());
        }
    }

    // 투표 생성 메소드
    @PostMapping("newvoting")
    public ResponseEntity<Object> votingInsert(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {

        String votingName = (String) payload.get("votingName");
        String projectId = String.valueOf(payload.get("projectId"));
        String createdUser = String.valueOf(payload.get("createdUser"));
        String detail = (String) payload.get("detail");
        LocalDateTime date = null;
        if (payload.get("selectedOption") != null) {
            date = (LocalDateTime) payload.get("selectedOption");
        }

        List<String> contents = (List<String>) payload.get("contents");


        contents.forEach(content -> System.out.println("Content: " + content));

        String token = request.getHeader("Authorization");


        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        List<Voting> result = projectService.votingInsert(votingName, projectId, createdUser, detail, date);

        if (result.size() > 0) {

            String listId = result.stream().map(Voting::getId).collect(Collectors.toSet()).toString();
            listId = listId.replaceAll("[\\[\\]]", "");
            boolean result2 = projectService.insertVoteContents(contents, Long.valueOf(listId));

            if (true) {
                return ResponseEntity.ok("항목 저장에 성공.");
            } else {
                return ResponseEntity.status(403).body("투표 항목 저장중에 실패 ");
            }
        } else {
            return ResponseEntity.status(403).body("투표 생성 중 오류가 발생했습니다.");
        }

    }

    // 투표 기본 정보 불러오기
    @PostMapping("findVoting")
    public ResponseEntity<Object> findVoting(
            @RequestParam("projectId") Long projectId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");


        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        List<Voting> vote = projectService.findByVoting(projectId);

        if (vote.isEmpty()) {
            return ResponseEntity.status(404).body(vote);
        } else {

            return ResponseEntity.ok(vote);
        }

    }

    @PostMapping("findContents") // 투표 contents 불러오기
    public ResponseEntity<Object> findContents(
            @RequestParam("votingId") Long votingId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }


        List<VotingContents> contents = projectService.findByVotingId(votingId);

        if (contents.isEmpty()) {
            return ResponseEntity.status(404).body(contents);
        } else {
            return ResponseEntity.ok(contents);
        }
    }


    // 유저가 투표한 투표 항목 (voting_record 테이블) insert
    @PostMapping("uservoteinsert")
    public ResponseEntity<Object> findUserVoting(
            @RequestParam("votingId") Long votingId, // 투표 고유 id
            @RequestParam("contentsId") Long contentsId, // 투표 한 항목 id
            @RequestParam("userId") Long userId, // user 고유 id
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");


        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        Boolean userVote = projectService.insertUserVote(votingId, contentsId, userId);
        if (userVote) {
            return ResponseEntity.ok("유저 투표 정보 등록 성공.");
        } else {
            return ResponseEntity.status(404).body("유저 투표 정보 등록 실패.");

        }
    }

    // 투표 별 (한사람) 유저가 투표한 항목 불러오기
    @PostMapping("findUserVoting")
    public ResponseEntity<Object> userVoteInsert(
            @RequestParam("votingId") Long votingId, // 투표 고유 id
            @RequestParam("userId") Long userId, // 투표 한 항목 id
            HttpServletRequest request) {


        // 1. 투표 고유 id 로 이 투표에 투표한 유저 id와 항목 정보를 불러온다.
        List<VotingRecord> uservoting = projectService.findByVotingIdRecord(votingId);


        if (uservoting.isEmpty()) {
            return ResponseEntity.status(404).body(uservoting);
        } else {
            System.out.println(uservoting);

            // 투표의 고유 id 를 가지고 온다.
            // 이제 가지고 해당 userId 가 있는지 확인한다.
            uservoting.getClass();
            uservoting.toString();


            for (VotingRecord user : uservoting) {
                if (user.getUserId().equals(userId)) { // 유저가 투표를 한 투표라면 true, 안했으면 false
                    List<Long> userVote = new ArrayList<>();
                    userVote.add(user.getVotingId()); // 투표 id
                    userVote.add(user.getContentsId()); // 유저가 투표 한 항목

                    return ResponseEntity.ok(userVote.toString());
                }
            }
            // 해당 유저 id 가 없다면(투표한 정보) null 반환
            return ResponseEntity.ok(null);
        }
    }

    // 유저들이 (여러사람)투표 한 항목 별 투표 수 조회하기
    @PostMapping("VoteOptionUsers")
    public ResponseEntity<Object> voteOptionUsers(
            @RequestParam("votingId") Long votingId, // 투표 id
            @RequestParam("projectId") Long projectId, // 프로젝트 id
            HttpServletRequest request) {

        try {
            List<Map<String, Object>> results = projectService.getVoteCounts(votingId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching vote counts.");
        }

    }

    // 공지사항 등록
    @PostMapping("newBoard")
    public ResponseEntity<Object> newBoard(
            @RequestParam("projectId") Long projectId,
            @RequestParam("boardTitle") String boardTitle,
            @RequestParam("boardContents") String boardContents,
            @RequestParam("boardBy") Long boardBy,
            HttpServletRequest request){


        boolean result = projectService.insertBoard(projectId, boardTitle,boardContents,boardBy);
        if (result) {
            return ResponseEntity.ok("공지사항 등록 성공");
        }else{
            return ResponseEntity.status(404).body("공지사항 등록 실패");
        }
    }

//    // 공지사항 list 조회
//    @PostMapping("findBoard")
//    public ResponseEntity<Object> findBoard(
//            @RequestParam("projectId") Long projectId){
//
//        List<Board> board = projectService.findByProjectId(projectId);
//
//        if(!board.isEmpty()){
//            return ResponseEntity.ok(board);
//        }else{
//            return ResponseEntity.status(404).body("공지사항 목록 조회에 실패하였습니다.");
//        }
//    }

    // 투표 생성자 정보 조회
    @PostMapping("votingByUser")
    public ResponseEntity<Object> votingByUser(
            @RequestParam("userId") Long userId){


        Optional<User> user = projectService.findById(userId);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body(user);
        }else{
            return ResponseEntity.ok(user);
        }
    }

    // 투표 상태(진행중 or 진행종료) 값 변경
    @PostMapping("isVoteUpdate")
    public ResponseEntity<Object> isVoteUpdate(
            @RequestParam("votingId") Long voteId){
        try {

            projectService.updateVoteStatus(voteId);
            return ResponseEntity.ok("투표 상태 변경 성공 !");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("투표 상태 변경 중 에러 : " + e.getMessage());
        }
    }

    // 다가오는 프로젝트 캘린더 일정 조회
    @PostMapping("calendarList")
    public ResponseEntity<Object> calendarList(
            @RequestParam("projectId") Long projectId,
            @RequestParam("userId") Long userId) {


        System.out.println("다가오는 캘린더 넘어옴");
        List<Calendar> calendars = calendarRepository.findByProjectId(projectId);
        System.out.println("Project calendar :: " + calendars);

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7); // 앞으로 7일간

        List<Map<String, Object>> upcomingCalendars = new ArrayList<>();


        for (Calendar calendar : calendars) {
            LocalDate startTime = calendar.getStartTime().toLocalDate();
            LocalDate endTime = calendar.getEndTime().toLocalDate();

            if (calendar.getCreatedBy().equals(userId) &&
                    (startTime.isBefore(sevenDaysLater) && startTime.isAfter(today.minusDays(1)) ||
                            endTime.isBefore(sevenDaysLater) && endTime.isAfter(today.minusDays(1)))) {

                Map<String, Object> calendarDetails = new HashMap<>();
                calendarDetails.put("title", calendar.getTitle());
                calendarDetails.put("start_time", calendar.getStartTime());
                calendarDetails.put("end_time", calendar.getEndTime());
                upcomingCalendars.add(calendarDetails);
            }
        }

        System.out.println("유저의 다가올 일정 :: " + upcomingCalendars);

        return ResponseEntity.ok(upcomingCalendars);
    }


    // 프로젝트 이름 변경
    @PostMapping("nameModify")
    public ResponseEntity<Object> modifyProjectName(
            @RequestParam("id") Long projectId,
            @RequestParam("name") String title) {

        try {
            projectService.updateProjectTitle(projectId, title);
            return ResponseEntity.ok("프로젝트 이름 변경 성공 !");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("프로젝트 이름 변경 중 에러 : " + e.getMessage());
        }
    }


    // 프로젝트 담당자 변경
    @PostMapping("managerModify")
    public ResponseEntity<Object> managerModify(
            @RequestParam("id") Long userId,
            @RequestParam("projectId") Long projectId){
        try {
            projectService.updateProjectCreatedBy(userId, projectId);
            return ResponseEntity.ok("프로젝트 담당자 변경 성공 !");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("프로젝트 담당자 변경 중 에러 : " + e.getMessage());
        }
    }

    // 등록된 중요 공지사항 조회
    @PostMapping("noticesSend")
    public ResponseEntity<Object> noticesSend(
            @RequestParam("projectId") Long projectId
    ){


        List<Notice> noticesList = noticeRepository.findTop3ByProjectIdAndImportantOrderByCreatedAtDesc(projectId, true);
        System.out.println("프로젝트에서 중요도 있는 공지사항 조회 :: " + noticesList);
        return ResponseEntity.ok(noticesList);
    }

}
