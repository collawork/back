package com.collawork.back.repository.project;

import com.collawork.back.model.project.VotingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingRecordRepository extends JpaRepository<VotingRecord, Long> {
    

    List<VotingRecord> findByVotingId(Long votingId);

    List<Object> findByContentsId(Long id);
}
