package com.collawork.back.repository.project;

import com.collawork.back.model.project.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {

    List<Voting> findByProjectId(Long projectId);

    // 현재 날짜보다 voting_end 가 지난 투표 조회
    @Query("SELECT v FROM Voting v WHERE v.votingEnd < :now AND v.isVote = true")
    List<Voting> findExpiredVotings(LocalDateTime now);

    // is_vote를 false로 업데이트
    @Modifying
    @Query("UPDATE Voting v SET v.isVote = false WHERE v.id IN :ids")
    void updateIsVoteToFalse(List<Long> ids);
}
