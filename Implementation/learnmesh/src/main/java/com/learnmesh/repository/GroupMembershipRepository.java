package com.learnmesh.repository;

import com.learnmesh.entity.GroupMembership;
import com.learnmesh.entity.StudyGroup;
import com.learnmesh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    // Check if a user is already a member of a group
    boolean existsByUserAndStudyGroup(User user, StudyGroup studyGroup);

    // Count members in a group
    int countByStudyGroup(StudyGroup studyGroup);

    // All groups a user has joined
    List<GroupMembership> findByUser(User user);

    // All members of a specific group
    List<GroupMembership> findByStudyGroup(StudyGroup group);

    // ⭐ REQUIRED FOR CASCADE DELETE
    void deleteByUser(User user);

    // ⭐ REQUIRED FOR CASCADE DELETE
    void deleteByStudyGroup(StudyGroup group);
}
