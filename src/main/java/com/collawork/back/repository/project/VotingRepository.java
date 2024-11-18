package com.collawork.back.repository.project;

import com.collawork.back.model.project.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {

}
