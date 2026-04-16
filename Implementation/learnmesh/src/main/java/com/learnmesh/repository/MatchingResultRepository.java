package com.learnmesh.repository;

import com.learnmesh.entity.MatchingResult;
import com.learnmesh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingResultRepository extends JpaRepository<MatchingResult, Long> {
    List<MatchingResult> findByUser(User user);
}
