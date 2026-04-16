package com.learnmesh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learnmesh.entity.StudyGroup;
import com.learnmesh.entity.User;
import com.learnmesh.repository.StudyGroupRepository;
import com.learnmesh.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMembershipService membershipService;

    // ---------------------------------------------------------
    // BASIC METHODS
    // ---------------------------------------------------------
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // ⭐ REMOVED getLoggedInUser() — controllers now handle login session

    // ---------------------------------------------------------
    // ADMIN FEATURES
    // ---------------------------------------------------------
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public long countUsers() {
        return userRepository.count();
    }

    // ⭐ FINAL FIX: CASCADE DELETE USER
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        if (user == null) return;

        // 1. Delete memberships where user is a member
        membershipService.deleteMembershipsByUser(user);

        // 2. Delete groups created by this user
        List<StudyGroup> createdGroups = studyGroupRepository.findAll()
                .stream()
                .filter(g -> g.getCreatedBy() != null && g.getCreatedBy().getId().equals(id))
                .toList();

        for (StudyGroup group : createdGroups) {
            membershipService.deleteMembershipsByGroup(group);
            studyGroupRepository.delete(group);
        }

        // 3. Delete the user
        userRepository.delete(user);
    }

    public void promoteToAdmin(Long id) {
        User user = findById(id);
        if (user != null) {
            user.setRole(User.Role.ADMIN);
            userRepository.save(user);
        }
    }
}
