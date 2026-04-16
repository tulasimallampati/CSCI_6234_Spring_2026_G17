package com.learnmesh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.learnmesh.entity.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
	
	List<StudyGroup> findByCreatedById(Long userId);


	@Query("SELECT gm.studyGroup FROM GroupMembership gm WHERE gm.user.id = :userId")
	List<StudyGroup> findGroupsUserJoined(Long userId);


}
