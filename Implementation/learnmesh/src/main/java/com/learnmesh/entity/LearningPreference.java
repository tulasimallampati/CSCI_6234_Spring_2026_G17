package com.learnmesh.entity;

import com.learnmesh.enums.Branch;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_preferences")
public class LearningPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELATIONSHIP
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ⭐ NEW: Branch enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Branch branch;

    // ⭐ UPDATED: Technology stored as String (value chosen from branch-specific enums)
    @Column(nullable = false)
    private String technology;

    // Knowledge level stays as String
    @Column(nullable = false)
    private String knowledgeLevel;

    @Column(columnDefinition = "TEXT")
    private String topicsToCover;

    @Column(nullable = false)
    private LocalDateTime preferredStartTime;

    @Column(nullable = false)
    private LocalDateTime preferredEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingMode preferredMeetingMode;

    public enum MeetingMode {
        ONLINE,
        IN_PERSON,
        HYBRID
    }

    public LearningPreference() {}

    public LearningPreference(User user, Branch branch, String technology, String knowledgeLevel,
                              String topicsToCover, LocalDateTime preferredStartTime,
                              LocalDateTime preferredEndTime, MeetingMode preferredMeetingMode) {
        this.user = user;
        this.branch = branch;
        this.technology = technology;
        this.knowledgeLevel = knowledgeLevel;
        this.topicsToCover = topicsToCover;
        this.preferredStartTime = preferredStartTime;
        this.preferredEndTime = preferredEndTime;
        this.preferredMeetingMode = preferredMeetingMode;
    }

    // GETTERS & SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getKnowledgeLevel() { return knowledgeLevel; }
    public void setKnowledgeLevel(String knowledgeLevel) { this.knowledgeLevel = knowledgeLevel; }

    public String getTopicsToCover() { return topicsToCover; }
    public void setTopicsToCover(String topicsToCover) { this.topicsToCover = topicsToCover; }

    public LocalDateTime getPreferredStartTime() { return preferredStartTime; }
    public void setPreferredStartTime(LocalDateTime preferredStartTime) { this.preferredStartTime = preferredStartTime; }

    public LocalDateTime getPreferredEndTime() { return preferredEndTime; }
    public void setPreferredEndTime(LocalDateTime preferredEndTime) { this.preferredEndTime = preferredEndTime; }

    public MeetingMode getPreferredMeetingMode() { return preferredMeetingMode; }
    public void setPreferredMeetingMode(MeetingMode preferredMeetingMode) { this.preferredMeetingMode = preferredMeetingMode; }
}
