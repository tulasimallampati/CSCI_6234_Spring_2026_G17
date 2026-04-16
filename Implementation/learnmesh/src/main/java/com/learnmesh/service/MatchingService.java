package com.learnmesh.service;

import com.learnmesh.entity.LearningPreference;
import com.learnmesh.entity.MatchingResult;
import com.learnmesh.entity.StudyGroup;
import com.learnmesh.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingService {

    private final StudyGroupService studyGroupService;

    public MatchingService(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    // Helper class for score + explanation
    public static class MatchExplanation {
        public int score;
        public List<String> reasons = new ArrayList<>();
    }

    // Main matching logic
    public List<MatchingResult> matchUserToGroups(User user,
                                                  LearningPreference pref,
                                                  List<List<String>> explanationHolder) {

        List<StudyGroup> allGroups = studyGroupService.findAll();
        List<MatchingResult> results = new ArrayList<>();

        for (StudyGroup group : allGroups) {

            MatchExplanation exp = calculateScore(pref, group);

            results.add(new MatchingResult(user, group, exp.score));
            explanationHolder.add(exp.reasons);
        }

        // Sort by score
        results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        // Limit to top 5
        if (results.size() > 5) {
            results = results.subList(0, 5);
            explanationHolder = explanationHolder.subList(0, 5);
        }

        return results;
    }

    // Score calculation (NO NEGATIVE POINTS)
    private MatchExplanation calculateScore(LearningPreference pref, StudyGroup group) {

        MatchExplanation exp = new MatchExplanation();

        // 1. Branch (25)
        if (pref.getBranch() == group.getBranch()) {
            exp.score += 25;
            exp.reasons.add("✓ Branch matches (+25)");
        } else {
            exp.reasons.add("✗ Branch does not match (–25)");
        }

        // 2. Technology (25)
        if (pref.getTechnology() != null &&
            pref.getTechnology().equalsIgnoreCase(group.getTechnology())) {

            exp.score += 25;
            exp.reasons.add("✓ Technology matches (+25)");
        } else {
            exp.reasons.add("✗ Technology does not match (–25)");
        }

        // 3. Knowledge Level (20)
        if (pref.getKnowledgeLevel() != null &&
            pref.getKnowledgeLevel().equalsIgnoreCase(group.getKnowledgeLevel().name())) {

            exp.score += 20;
            exp.reasons.add("✓ Knowledge level matches (+20)");
        } else {
            exp.reasons.add("✗ Knowledge level does not match (–20)");
        }

        // 4. Meeting Mode (10)
        if (pref.getPreferredMeetingMode() != null &&
            group.getMeetingMode() != null &&
            pref.getPreferredMeetingMode().name().equals(group.getMeetingMode().name())) {

            exp.score += 10;
            exp.reasons.add("✓ Meeting mode matches (+10)");
        } else {
            exp.reasons.add("✗ Meeting mode does not match (–10)");
        }

        // 5. Time Matching (20 exact, 10 partial, 0 none)
        LocalDateTime ps = pref.getPreferredStartTime();
        LocalDateTime pe = pref.getPreferredEndTime();
        LocalDateTime gs = group.getStartTime();
        LocalDateTime ge = group.getEndTime();

        if (ps.equals(gs) && pe.equals(ge)) {
            exp.score += 20;
            exp.reasons.add("🔥 Exact time match (+20)");
        }
        else if (ps.isBefore(ge) && pe.isAfter(gs)) {
            exp.score += 10;
            exp.reasons.add("⏳ Partial time overlap (+10)");
            exp.reasons.add("• Reduced because your time does not fully match the group time");
            exp.reasons.add("• Your time: " + ps + " to " + pe);
            exp.reasons.add("• Group time: " + gs + " to " + ge);
        }
        else {
            exp.reasons.add("✗ No time overlap (–20)");
            exp.reasons.add("• Reduced because your availability does not intersect with the group time");
            exp.reasons.add("• Your time: " + ps + " to " + pe);
            exp.reasons.add("• Group time: " + gs + " to " + ge);
        }

        return exp;
    }
}
