package com.learnmesh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learnmesh.entity.GroupMembership;
import com.learnmesh.entity.StudyGroup;
import com.learnmesh.entity.User;
import com.learnmesh.repository.GroupMembershipRepository;

@Service
public class GroupMembershipService {

    private final GroupMembershipRepository membershipRepo;

    public GroupMembershipService(GroupMembershipRepository membershipRepo) {
        this.membershipRepo = membershipRepo;
    }

    // ---------------------------------------------------------
    // TIME CONFLICT CHECK
    // ---------------------------------------------------------
    public boolean hasTimeConflict(User user, StudyGroup newGroup) {
        List<GroupMembership> memberships = membershipRepo.findByUser(user);

        if (newGroup.getStartTime() == null || newGroup.getEndTime() == null) {
            return false;
        }

        for (GroupMembership m : memberships) {
            StudyGroup existing = m.getStudyGroup();

            if (existing.getStartTime() == null || existing.getEndTime() == null) {
                continue;
            }

            boolean overlap =
                    newGroup.getStartTime().isBefore(existing.getEndTime()) &&
                    newGroup.getEndTime().isAfter(existing.getStartTime());

            if (overlap) {
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------
    // LEADER AUTO-MEMBERSHIP
    // ---------------------------------------------------------
    public void addLeaderAsMember(User user, StudyGroup group) {
        if (!membershipRepo.existsByUserAndStudyGroup(user, group)) {
            GroupMembership membership = new GroupMembership();
            membership.setUser(user);
            membership.setStudyGroup(group);
            membershipRepo.save(membership);
        }
    }

    // ---------------------------------------------------------
    // JOIN GROUP
    // ---------------------------------------------------------
    public String joinGroup(User user, StudyGroup group) {
        if (membershipRepo.existsByUserAndStudyGroup(user, group)) {
            return "You are already a member of this group.";
        }

        int currentMembers = membershipRepo.countByStudyGroup(group);
        if (group.getMaxMembers() != null && currentMembers >= group.getMaxMembers()) {
            return "This group is full.";
        }

        if (hasTimeConflict(user, group)) {
            return "You already have another group during this time.";
        }

        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setStudyGroup(group);

        membershipRepo.save(membership);

        return "JOINED";
    }

    // ---------------------------------------------------------
    // LEAVE GROUP
    // ---------------------------------------------------------
    public void leaveGroup(User user, StudyGroup group) {
        List<GroupMembership> memberships = membershipRepo.findByUser(user);

        memberships.stream()
                .filter(m -> m.getStudyGroup().getId().equals(group.getId()))
                .findFirst()
                .ifPresent(membershipRepo::delete);
    }

    // ---------------------------------------------------------
    // CHECK MEMBERSHIP
    // ---------------------------------------------------------
    public boolean isMember(User user, StudyGroup group) {
        return membershipRepo.existsByUserAndStudyGroup(user, group);
    }

    public boolean isUserMemberOfGroup(User user, StudyGroup group) {
        return isMember(user, group);
    }

    // ---------------------------------------------------------
    // MEMBER COUNT
    // ---------------------------------------------------------
    public int countMembers(StudyGroup group) {
        return membershipRepo.countByStudyGroup(group);
    }

    // ---------------------------------------------------------
    // GET GROUPS USER JOINED
    // ---------------------------------------------------------
    public List<StudyGroup> getGroupsUserJoined(User user) {
        return membershipRepo.findByUser(user)
                .stream()
                .map(GroupMembership::getStudyGroup)
                .toList();
    }

    public List<User> getMembersOfGroup(StudyGroup group) {
        return membershipRepo.findByStudyGroup(group)
                .stream()
                .map(GroupMembership::getUser)
                .toList();
    }

    // ---------------------------------------------------------
    // ⭐ ADMIN CASCADE DELETE HELPERS
    // ---------------------------------------------------------

    // Delete all memberships where user is a member
    public void deleteMembershipsByUser(User user) {
        membershipRepo.deleteByUser(user);
    }

    // Delete all memberships of a group
    public void deleteMembershipsByGroup(StudyGroup group) {
        membershipRepo.deleteByStudyGroup(group);
    }
}
