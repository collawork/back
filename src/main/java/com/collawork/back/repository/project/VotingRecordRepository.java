package com.collawork.back.repository.project;

import com.collawork.back.model.project.VotingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingRecordRepository extends JpaRepository<VotingRecord, Long> {
}
