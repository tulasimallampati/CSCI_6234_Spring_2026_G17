package com.learnmesh.controller;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.learnmesh.entity.LearningPreference;
import com.learnmesh.entity.LoginSession;
import com.learnmesh.entity.User;
import com.learnmesh.enums.AeronauticalTechnology;
import com.learnmesh.enums.BiomedicalTechnology;
import com.learnmesh.enums.Branch;
import com.learnmesh.enums.CSTechnology;
import com.learnmesh.enums.ChemicalTechnology;
import com.learnmesh.enums.CivilTechnology;
import com.learnmesh.enums.ElectricalTechnology;
import com.learnmesh.enums.ElectronicsTechnology;
import com.learnmesh.enums.MechanicalTechnology;
import com.learnmesh.service.LearningPreferenceService;
import com.learnmesh.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LearningPreferenceController {

    private final LearningPreferenceService preferenceService;
    private final UserService userService;

    public LearningPreferenceController(LearningPreferenceService preferenceService,
                                        UserService userService) {
        this.preferenceService = preferenceService;
        this.userService = userService;
    }

    // ---------------------------------------------------------
    // ⭐ SET PREFERENCES (CREATE NEW)
    // ---------------------------------------------------------
    @GetMapping("/preferences")
    public String showPreferences(Model model, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("preference", new LearningPreference());
        model.addAttribute("branches", Branch.values());
        model.addAttribute("technologies", List.of());
        model.addAttribute("levels", List.of("BEGINNER", "INTERMEDIATE", "EXPERT"));
        model.addAttribute("modes", LearningPreference.MeetingMode.values());

        return "set-preferences";
    }

    @PostMapping("/preferences")
    public String savePreferences(@ModelAttribute LearningPreference preference,
                                  HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        Long userId = LoginSession.getUserId(session);
        User user = userService.findById(userId);

        preferenceService.savePreference(preference, user);

        return "redirect:/my-preferences?saved=true";
    }

    // ---------------------------------------------------------
    // ⭐ DYNAMIC TECHNOLOGY LOADING
    // ---------------------------------------------------------
    @GetMapping("/preferences/technologies")
    @ResponseBody
    public List<String> getTechnologiesByBranch(@RequestParam("branch") Branch branch) {

        return switch (branch) {
            case COMPUTER_SCIENCE, INFORMATION_TECHNOLOGY ->
                    Arrays.stream(CSTechnology.values()).map(Enum::name).toList();

            case MECHANICAL_ENGINEERING ->
                    Arrays.stream(MechanicalTechnology.values()).map(Enum::name).toList();

            case CIVIL_ENGINEERING ->
                    Arrays.stream(CivilTechnology.values()).map(Enum::name).toList();

            case ELECTRICAL_ENGINEERING ->
                    Arrays.stream(ElectricalTechnology.values()).map(Enum::name).toList();

            case ELECTRONICS_ENGINEERING ->
                    Arrays.stream(ElectronicsTechnology.values()).map(Enum::name).toList();

            case CHEMICAL_ENGINEERING ->
                    Arrays.stream(ChemicalTechnology.values()).map(Enum::name).toList();

            case AERONAUTICAL_ENGINEERING ->
                    Arrays.stream(AeronauticalTechnology.values()).map(Enum::name).toList();

            case BIOMEDICAL_ENGINEERING ->
                    Arrays.stream(BiomedicalTechnology.values()).map(Enum::name).toList();
        };
    }

    // ---------------------------------------------------------
    // ⭐ VIEW MY PREFERENCES
    // ---------------------------------------------------------
    @GetMapping("/my-preferences")
    public String viewMyPreferences(Model model, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        Long userId = LoginSession.getUserId(session);
        User user = userService.findById(userId);

        model.addAttribute("preferences", preferenceService.getPreferencesForUser(user));

        return "my-preferences";
    }

    // ---------------------------------------------------------
    // ⭐ EDIT PREFERENCE (with datetime formatting)
    // ---------------------------------------------------------
    @GetMapping("/preferences/{id}/edit")
    public String editPreference(@PathVariable Long id, Model model, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        Long userId = LoginSession.getUserId(session);
        User user = userService.findById(userId);

        LearningPreference pref = preferenceService.getById(id);

        if (pref == null || pref.getUser() == null || !pref.getUser().getId().equals(user.getId())) {
            return "redirect:/my-preferences?invalid=true";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String formattedStart = pref.getPreferredStartTime() != null
                ? pref.getPreferredStartTime().format(formatter)
                : "";

        String formattedEnd = pref.getPreferredEndTime() != null
                ? pref.getPreferredEndTime().format(formatter)
                : "";

        model.addAttribute("formattedStart", formattedStart);
        model.addAttribute("formattedEnd", formattedEnd);

        model.addAttribute("preference", pref);
        model.addAttribute("branches", Branch.values());
        model.addAttribute("levels", List.of("BEGINNER", "INTERMEDIATE", "EXPERT"));
        model.addAttribute("modes", LearningPreference.MeetingMode.values());

        return "edit-preference";
    }

    // ---------------------------------------------------------
    // ⭐ UPDATE PREFERENCE
    // ---------------------------------------------------------
    @PostMapping("/preferences/update")
    public String updatePreference(@ModelAttribute LearningPreference preference,
                                   HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        Long userId = LoginSession.getUserId(session);
        User user = userService.findById(userId);

        preferenceService.updatePreference(preference, user);

        return "redirect:/my-preferences?updated=true";
    }

    // ---------------------------------------------------------
    // ⭐ DELETE PREFERENCE
    // ---------------------------------------------------------
    @PostMapping("/preferences/{id}/delete")
    public String deletePreference(@PathVariable Long id, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        Long userId = LoginSession.getUserId(session);
        User user = userService.findById(userId);

        preferenceService.deletePreference(id, user);

        return "redirect:/my-preferences?deleted=true";
    }
}
