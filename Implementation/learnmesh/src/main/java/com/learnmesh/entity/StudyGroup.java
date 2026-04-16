package com.learnmesh.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.learnmesh.enums.Branch;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BASIC INFORMATION

    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 50, message = "Group name must be 3–50 characters")
    private String name;

    // ⭐ NEW: Branch enum
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Branch is required")
    private Branch branch;

    // ⭐ UPDATED: Technology (string, but values come from branch-specific enums)
    @NotBlank(message = "Technology / Course is required")
    private String technology;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 300, message = "Description must be 10–300 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Knowledge level is required")
    private KnowledgeLevel knowledgeLevel;

    // Optional
    private String topicsToCover;

    // SCHEDULING

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;


    @Enumerated(EnumType.STRING)
    private MeetingFrequency meetingFrequency; // optional

    @NotNull(message = "Meeting mode is required")
    @Enumerated(EnumType.STRING)
    private MeetingMode meetingMode;

    private String meetingLocation;

    // CAPACITY

    @NotNull(message = "Maximum members is required")
    @Min(value = 2, message = "Group must allow at least 2 members")
    @Max(value = 100, message = "Group cannot exceed 100 members")
    private Integer maxMembers;

    // OPTIONAL EXTRAS

    private String requiredMaterials;

    // RELATIONSHIP

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // ENUMS

    public enum KnowledgeLevel {
        BEGINNER,
        INTERMEDIATE,
        EXPERT
    }

    public enum MeetingFrequency {
        ONE_TIME,
        WEEKLY,
        BI_WEEKLY,
        MONTHLY
    }

    public enum MeetingMode {
        ONLINE,
        IN_PERSON,
        HYBRID
    }

    // GETTERS AND SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public KnowledgeLevel getKnowledgeLevel() { return knowledgeLevel; }
    public void setKnowledgeLevel(KnowledgeLevel knowledgeLevel) { this.knowledgeLevel = knowledgeLevel; }

    public String getTopicsToCover() { return topicsToCover; }
    public void setTopicsToCover(String topicsToCover) { this.topicsToCover = topicsToCover; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public MeetingFrequency getMeetingFrequency() { return meetingFrequency; }
    public void setMeetingFrequency(MeetingFrequency meetingFrequency) { this.meetingFrequency = meetingFrequency; }

    public MeetingMode getMeetingMode() { return meetingMode; }
    public void setMeetingMode(MeetingMode meetingMode) { this.meetingMode = meetingMode; }

    public String getMeetingLocation() { return meetingLocation; }
    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public String getRequiredMaterials() { return requiredMaterials; }
    public void setRequiredMaterials(String requiredMaterials) { this.requiredMaterials = requiredMaterials; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
