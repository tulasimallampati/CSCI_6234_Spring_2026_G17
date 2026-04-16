package com.learnmesh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learnmesh.entity.StudyGroup;
import com.learnmesh.repository.StudyGroupRepository;

@Service
public class StudyGroupService {

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMembershipService membershipService;

    // ---------------------------------------------------------
    // BASIC CRUD
    // ---------------------------------------------------------
    public StudyGroup saveGroup(StudyGroup group) {
        return studyGroupRepository.save(group);
    }
    
    public StudyGroup findById(Long id) {
        return studyGroupRepository.findById(id).orElse(null);
    }

    // ⭐ FIXED: CASCADE DELETE FOR ADMIN
    @Transactional
    public void deleteGroup(Long id) {
        StudyGroup group = findById(id);
        if (group != null) {
            // 1. Delete all memberships of this group
            membershipService.deleteMembershipsByGroup(group);

            // 2. Delete the group itself
            studyGroupRepository.delete(group);
        }
    }

    public List<StudyGroup> findAll() {
        return studyGroupRepository.findAll();
    }

    // ---------------------------------------------------------
    // ADMIN DASHBOARD STATS
    // ---------------------------------------------------------
    public long countGroups() {
        return studyGroupRepository.count();
    }

    public long countActiveGroups() {
        return studyGroupRepository.findAll().stream()
                .filter(g -> g.getEndTime() == null || g.getEndTime().isAfter(java.time.LocalDateTime.now()))
                .count();
    }

    public long countFullGroups() {
        return studyGroupRepository.findAll().stream()
                .filter(g -> {
                    int members = membershipService.countMembers(g);
                    return g.getMaxMembers() != null && members >= g.getMaxMembers();
                })
                .count();
    }
    
    public List<StudyGroup> findGroupsByLeader(Long userId) {
        return studyGroupRepository.findByCreatedById(userId);
    }

    public List<StudyGroup> findGroupsUserJoined(Long userId) {
        return studyGroupRepository.findGroupsUserJoined(userId);
    }



}
