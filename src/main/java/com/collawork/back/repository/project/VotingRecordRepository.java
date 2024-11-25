package com.collawork.back.repository.project;

import com.collawork.back.model.project.VotingRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingRecordRepository extends JpaRepository<VotingRecord, Long> {

    @Query("SELECT vr.contentsId AS contentsId, COUNT(vr.userId) AS userCount " +
            "FROM VotingRecord vr " +
            "WHERE vr.votingId = :votingId " +
            "GROUP BY vr.contentsId")
    List<VoteCountProjection> countUserVotesByVotingId(@Param("votingId") Long votingId);
    

    List<VotingRecord> findByVotingId(Long votingId);

    List<Object> findByContentsId(Long id);
}
