package com.collawork.back.repository.project;

import com.collawork.back.model.project.VotingContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingContentsRepository extends JpaRepository<VotingContents, Long> {
    List<VotingContents> findByVotingId(Long votingId);
}
