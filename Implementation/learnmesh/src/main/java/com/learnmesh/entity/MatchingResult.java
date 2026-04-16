package com.learnmesh.entity;

import jakarta.persistence.*;

@Entity
public class MatchingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    private int score;

    public MatchingResult() {}

    public MatchingResult(User user, StudyGroup studyGroup, int score) {
        this.user = user;
        this.studyGroup = studyGroup;
        this.score = score;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public StudyGroup getStudyGroup() { return studyGroup; }
    public void setStudyGroup(StudyGroup studyGroup) { this.studyGroup = studyGroup; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
