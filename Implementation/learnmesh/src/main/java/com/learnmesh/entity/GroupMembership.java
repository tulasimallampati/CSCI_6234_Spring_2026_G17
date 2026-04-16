package com.learnmesh.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many memberships → one user
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Many memberships → one study group
    @ManyToOne(optional = false)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    private LocalDateTime joinedAt;

    @PrePersist
    public void setJoinTime() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public StudyGroup getStudyGroup() { return studyGroup; }
    public void setStudyGroup(StudyGroup studyGroup) { this.studyGroup = studyGroup; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
