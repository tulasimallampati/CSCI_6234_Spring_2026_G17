package com.learnmesh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learnmesh.entity.LearningPreference;
import com.learnmesh.entity.LoginSession;
import com.learnmesh.entity.MatchingResult;
import com.learnmesh.entity.User;
import com.learnmesh.service.LearningPreferenceService;
import com.learnmesh.service.MatchingService;
import com.learnmesh.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RecommendationController {

    private final UserService userService;
    private final LearningPreferenceService preferenceService;
    private final MatchingService matchingService;

    public RecommendationController(UserService userService,
                                    LearningPreferenceService preferenceService,
                                    MatchingService matchingService) {
        this.userService = userService;
        this.preferenceService = preferenceService;
        this.matchingService = matchingService;
    }

    @GetMapping("/recommendations")
    public String showRecommendations(
            @RequestParam(value = "prefId", required = false) Long prefId,
            HttpSession session,
            Model model) {

        // ⭐ NEW — require login
        if (!LoginSession.isLoggedIn(session)) {
            return "redirect:/login";
        }

        Long userId = LoginSession.getUserId(session);
        User user = userService.findById(userId);

        // Load all preferences for this user
        List<LearningPreference> prefs = preferenceService.getPreferencesForUser(user);
        model.addAttribute("preferences", prefs);

        // If no preference selected → show only list
        if (prefId == null) {
            model.addAttribute("results", null);
            return "recommendations";
        }

        // Load selected preference
        LearningPreference selectedPref = preferenceService.getById(prefId);

        // Prevent accessing someone else's preference
        if (selectedPref == null || !selectedPref.getUser().getId().equals(user.getId())) {
            return "redirect:/recommendations?invalidPref=true";
        }

        model.addAttribute("selectedPref", selectedPref);

        // Run matching
        List<List<String>> explanations = new ArrayList<>();
        List<MatchingResult> results =
                matchingService.matchUserToGroups(user, selectedPref, explanations);

        model.addAttribute("results", results);
        model.addAttribute("explanations", explanations);

        return "recommendations";
    }
}
