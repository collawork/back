use collawork;  


drop table users;
drop table chat_rooms;
drop table projects;
drop table friends;
drop table chat_room_participants;
drop table messages;
drop table project_participants;
drop table calendar_events; 
drop table notifications;

drop table notices;
drop table categories;
drop table category_friends;

drop table voting;
drop table voting_contents;
drop table voting_record;

drop table project_board;
drop table board_comment;



-- 1. 사용자 정보를 저장
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 사용자 고유 ID
    username VARCHAR(255) NOT NULL,       -- 사용자 이름, 고유값
    email VARCHAR(255) UNIQUE,                   -- 사용자 이메일, 고유값
    password VARCHAR(255),                       -- 사용자 비밀번호 (자체 회원가입 시 사용)
    oauth_provider VARCHAR(50),                  -- 소셜 로그인 제공자 (예: kakao, google, naver)
    oauth_id VARCHAR(255),                       -- 소셜 로그인 사용자 ID
    profile_image VARCHAR(255),                  -- 프로필 사진 URL
    company VARCHAR(255),                        -- 회사명
    position VARCHAR(255),                       -- 직급
    phone VARCHAR(20),                           -- 핸드폰 번호
    fax VARCHAR(20),                             -- 팩스 번호
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 계정 생성일
);

-- 2. 프로젝트 정보를 저장
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 프로젝트 고유 ID
    project_name VARCHAR(255) NOT NULL,          -- 프로젝트 이름
    created_by BIGINT,                           -- 프로젝트 생성자 ID (users 테이블 참조)
    project_code VARCHAR(255),            -- 프로젝트 고유 코드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 프로젝트 생성일
    chat_room_id BIGINT,                     -- 프로젝트 채팅방
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL, -- 생성자 ID가 삭제되면 NULL로 설정
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id)
);

-- 3. 친구 관계를 관리
CREATE TABLE friends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 친구 관계 고유 ID
    requester_id BIGINT NOT NULL,                -- 친구 요청을 보낸 사용자 ID (users 테이블 참조)
    responder_id BIGINT NOT NULL,                -- 친구 요청을 받은 사용자 ID (users 테이블 참조)
    status ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING', -- 친구 요청 상태
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 친구 요청일
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE, -- 요청자 삭제 시 관계도 삭제
    FOREIGN KEY (responder_id) REFERENCES users(id) ON DELETE CASCADE  -- 응답자 삭제 시 관계도 삭제
);

-- 4. 채팅방 정보를 저장
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 채팅방 고유 ID
    room_name VARCHAR(255),                      -- 채팅방 이름
    created_by BIGINT,                           -- 채팅방 생성자 ID (users 테이블 참조)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 채팅방 생성일
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL -- 생성자 삭제 시 NULL로 설정
);

-- 5. 채팅방과 참가자 관계를 관리
CREATE TABLE chat_room_participants (
    chat_room_id BIGINT NOT NULL,                -- 채팅방 ID (chat_rooms 테이블 참조)
    user_id BIGINT NOT NULL,                     -- 참가자 사용자 ID (users 테이블 참조)
    PRIMARY KEY (chat_room_id, user_id),         -- 채팅방 ID와 사용자 ID의 복합 기본 키
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE, -- 채팅방 삭제 시 참가자도 삭제
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE            -- 사용자 삭제 시 참가자 관계 삭제
);

-- 6. 채팅 메시지를 저장
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 메시지 고유 ID
    sender_id BIGINT,                            -- 메시지 보낸 사용자 ID (users 테이블 참조)
    chat_room_id BIGINT NOT NULL,                -- 메시지가 속한 채팅방 ID (chat_rooms 테이블 참조)
    content TEXT,                                -- 메시지 내용
    message_type ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT', -- 메시지 유형 (텍스트, 이미지, 파일)
    file_url VARCHAR(255),                       -- 파일 업로드 시 파일 URL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 메시지 보낸 시간
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL, -- 보낸 사용자 삭제 시 NULL로 설정
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE -- 채팅방 삭제 시 메시지도 삭제
);

-- 7. 프로젝트와 참가자 관계를 관리
CREATE TABLE project_participants (
    project_id BIGINT NOT NULL,                      -- 프로젝트 ID (projects 테이블 참조)
    user_id BIGINT NOT NULL,                         -- 사용자 ID (users 테이블 참조)
    role ENUM('MEMBER', 'ADMIN') DEFAULT 'MEMBER',   -- 참가자의 역할 (멤버, 관리자)
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING', -- 초대 상태
    PRIMARY KEY (project_id, user_id),               -- 프로젝트 ID와 사용자 ID의 복합 기본 키
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE, -- 프로젝트 삭제 시 참가자도 삭제
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE        -- 사용자 삭제 시 참가자 관계 삭제
);

-- 8. 프로젝트 일정 정보를 저장
CREATE TABLE calendar_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 일정 고유 ID
    project_id BIGINT,                         -- 프로젝트 ID (projects 테이블 참조)             
    title VARCHAR(255),                        -- 일정 제목                                  
    description TEXT,                            -- 일정 설명
    -- date TIMESTAMP,                         -- 
    start_time TIMESTAMP NOT NULL,               -- 일정 시작 시간                              
    end_time TIMESTAMP,                          -- 일정 종료 시간                               
    created_by BIGINT NOT NULL,                  -- 일정 생성자 ID (users 테이블 참조)             
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), -- 일정 생성일
    all_day BOOLEAN NOT NULL,                       -- 일정의 종일 표시 여부
    -- editable BOOLEAN,                     -- 일정 수정 가능 여부
    color VARCHAR(255),                     -- 일정 분류 색
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE, 
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE 
);



-- 9. 사용자 알림 정보를 저장
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,             -- 알림 고유 ID
    user_id BIGINT NOT NULL,                          -- 알림을 받을 사용자 ID (users 테이블 참조)
    type ENUM('FRIEND_REQUEST', 'MESSAGE', 'EVENT', 'PROJECT_INVITATION') NOT NULL, -- 알림 유형
    message TEXT,                                     -- 알림 내용
    is_read BOOLEAN DEFAULT FALSE,                    -- 읽음 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 알림 생성일
    friend_request_id BIGINT,                         -- 친구 요청 ID (friends 테이블 참조)
    project_id BIGINT,                                -- 프로젝트 초대 ID (projects 테이블 참조)
    responder_id BIGINT,                              -- 요청을 받은 사용자 ID
    is_action_completed BOOLEAN NOT NULL DEFAULT FALSE, -- 최종 처리 상태
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_request_id) REFERENCES friends(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (responder_id) REFERENCES users(id) ON DELETE CASCADE
);


-- 10. 투표 정보를 저장
CREATE TABLE voting (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 투표 고유 ID
    voting_name VARCHAR(255) NOT NULL,     -- 투표 제목
    voting_detail VARCHAR(255),           -- 투표 설명
    project_id BIGINT NOT NULL, -- (투표가 속한) 프로젝트 고유 id(projects 테이블 참조)
    created_user VARCHAR(255) NOT NULL,  -- 프로젝트 생성자 user id
    voting_end TIMESTAMP,                -- 투표 마감일 (지정일 or null)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 투표 생성일
    is_vote BOOLEAN DEFAULT TRUE, -- 투표 진행 여부(true, false) // 진행중|종료
    FOREIGN KEY ( project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- 11. 투표 항목 정보 저장
CREATE TABLE voting_contents(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,           -- 투표 항목 고유 id
    voting_id BIGINT,                      -- 투표 id (voting 테이블 참조)
    voting_contents VARCHAR(255),                    -- 투표한 항목 내용 저장
     FOREIGN KEY (voting_id) REFERENCES voting(id) ON DELETE SET NULL -- 투표 삭제 시 같이 삭제
);



-- 12. 유저 투표 항목 정보 기록
CREATE TABLE voting_record(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 투표 기록 고유 id
   voting_id BIGINT,                        -- 투표 고유 id (voting 테이블 참조)
   contents_id BIGINT,                      -- 투표 항목 고유 id (voting_contents 테이블 참조)
    user_id BIGINT,                          -- 투표 한 사용자 id 
    FOREIGN KEY (voting_id) REFERENCES voting(id) ON DELETE CASCADE,  
   FOREIGN KEY (contents_id) REFERENCES voting_contents(id) ON DELETE CASCADE
    );
    
    -- 13. 공지사항 정보 기록
CREATE TABLE project_board(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 공지사항 고유 id
    project_id BIGINT NOT NULL,               -- (공지사항이 속한) 프로젝트 고유 id(projects 테이블 참조)
    board_title VARCHAR(255) NOT NULL,        -- 공지사항 제목
    board_contents VARCHAR(255),              -- 공지사항 내용
    board_by BIGINT,                          -- 공지사항 작성자 id(user 테이블 참조)
    board_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 공지사항 작성 시간
   FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (board_by) REFERENCES projects(id) ON DELETE CASCADE
     );
     
-- 14.공지사항 댓글 정보 기록
CREATE TABLE board_comment(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 댓글 고유 id
    board_id BIGINT NOT NULL,                  -- 공지사항 고유 id(project_board 테이블 참조)
    comment_by BIGINT,                        -- 댓글 작성자 id(user 테이블 참조)
    comment_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 댓글 작성 시간
    comment_state BIGINT,                         -- 받은 공감 수
    FOREIGN KEY (board_id) REFERENCES project_board(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_by) REFERENCES users(id) ON DELETE CASCADE
    );
    
-- 15. 친구 카테고리      
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 카테고리 고유 키값
    user_id BIGINT NOT NULL,               -- 사용자 아이디
    name VARCHAR(255) NOT NULL,               -- 카테고리 이름
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE   
);

-- 16. 카테고리-친구 연관관계 
CREATE TABLE category_friends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 연관관계 고유 키값
    category_id BIGINT NOT NULL,            -- 카테고리 아이디
    friend_id BIGINT NOT NULL,               -- 친구 아이디
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);   

-- 17. 프로젝트 진행률 저장
CREATE TABLE project_percentage(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 프로젝트 진행률 고유 id
    project_id BIGINT NOT NULL,                     -- 프로젝트 ID (projects 테이블 참조)
    percent BIGINT,                      -- 프로젝트 진행률
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE -- 프로젝트 삭제 시 진행률 삭제
);

CREATE TABLE notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,           -- 공지사항 ID
    project_id BIGINT NOT NULL,                     -- 프로젝트 ID (projects 테이블 참조)
    title VARCHAR(255) NOT NULL,                    -- 공지사항 제목
    content TEXT NOT NULL,                          -- 공지사항 내용
    important BOOLEAN DEFAULT FALSE,                -- 공지사항 중요 여부
    creator_id BIGINT NOT NULL,                     -- 작성자 ID (users 테이블 참조)
    creator_name VARCHAR(255) NOT NULL,             -- 작성자 이름
    attachments JSON DEFAULT NULL,                  -- 첨부파일 정보 (JSON 형태)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 작성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    view_count INT NOT NULL DEFAULT 0,              -- 조회수
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE, -- 프로젝트 삭제 시 공지사항 삭제
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE     -- 작성자 삭제 시 공지사항 삭제
);