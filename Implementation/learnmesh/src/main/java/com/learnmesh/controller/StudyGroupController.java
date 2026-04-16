package com.learnmesh.controller;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.learnmesh.entity.LoginSession;
import com.learnmesh.entity.StudyGroup;
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
import com.learnmesh.service.GroupMembershipService;
import com.learnmesh.service.StudyGroupService;
import com.learnmesh.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class StudyGroupController {

    @Autowired
    private StudyGroupService studyGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupMembershipService membershipService;

    private static final DateTimeFormatter HTML_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // ---------------------------------------------------------
    // CREATE GROUP (FORM)
    // ---------------------------------------------------------
    @GetMapping("/groups/create")
    public String showCreateGroupForm(Model model, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("studyGroup", new StudyGroup());
        model.addAttribute("levels", StudyGroup.KnowledgeLevel.values());
        model.addAttribute("frequencies", StudyGroup.MeetingFrequency.values());
        model.addAttribute("modes", StudyGroup.MeetingMode.values());
        model.addAttribute("branches", Branch.values());
        model.addAttribute("technologies", List.of());

        model.addAttribute("formattedStartTime", "");
        model.addAttribute("formattedEndTime", "");

        return "create-group";
    }

    // ---------------------------------------------------------
    // DYNAMIC TECHNOLOGY LOADING
    // ---------------------------------------------------------
    @GetMapping("/groups/technologies")
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
    // CREATE GROUP (SUBMIT)
    // ---------------------------------------------------------
    @PostMapping("/groups/create")
    public String createGroup(@Valid @ModelAttribute("studyGroup") StudyGroup studyGroup,
                              BindingResult result,
                              HttpSession session,
                              Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        if (studyGroup.getMeetingMode() == StudyGroup.MeetingMode.IN_PERSON ||
            studyGroup.getMeetingMode() == StudyGroup.MeetingMode.HYBRID) {

            if (studyGroup.getMeetingLocation() == null ||
                studyGroup.getMeetingLocation().trim().isEmpty()) {

                result.rejectValue("meetingLocation", "error.meetingLocation",
                        "Meeting location is required for in-person or hybrid mode");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("levels", StudyGroup.KnowledgeLevel.values());
            model.addAttribute("frequencies", StudyGroup.MeetingFrequency.values());
            model.addAttribute("modes", StudyGroup.MeetingMode.values());
            model.addAttribute("branches", Branch.values());
            model.addAttribute("technologies", List.of());

            model.addAttribute("formattedStartTime",
                    studyGroup.getStartTime() != null ? studyGroup.getStartTime().format(HTML_DATETIME) : "");
            model.addAttribute("formattedEndTime",
                    studyGroup.getEndTime() != null ? studyGroup.getEndTime().format(HTML_DATETIME) : "");

            return "create-group";
        }

        User creator = userService.findById(userId);
        studyGroup.setCreatedBy(creator);

        studyGroupService.saveGroup(studyGroup);
        membershipService.addLeaderAsMember(creator, studyGroup);

        return "redirect:/my-groups?created=true";
    }

    // ---------------------------------------------------------
    // EDIT GROUP (FORM)
    // ---------------------------------------------------------
    @GetMapping("/groups/{id}/edit")
    public String editGroup(@PathVariable Long id, HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        StudyGroup group = studyGroupService.findById(id);
        if (group == null) return "redirect:/my-groups";

        if (!userId.equals(group.getCreatedBy().getId()))
            return "redirect:/my-groups";

        model.addAttribute("studyGroup", group);

        model.addAttribute("levels", StudyGroup.KnowledgeLevel.values());
        model.addAttribute("frequencies", StudyGroup.MeetingFrequency.values());
        model.addAttribute("modes", StudyGroup.MeetingMode.values());
        model.addAttribute("branches", Branch.values());
        model.addAttribute("technologies", List.of());

        model.addAttribute("formattedStartTime",
                group.getStartTime() != null ? group.getStartTime().format(HTML_DATETIME) : "");
        model.addAttribute("formattedEndTime",
                group.getEndTime() != null ? group.getEndTime().format(HTML_DATETIME) : "");

        return "create-group";
    }

    // ---------------------------------------------------------
    // EDIT GROUP (SUBMIT)
    // ---------------------------------------------------------
    @PostMapping("/groups/{id}/edit")
    public String updateGroup(@PathVariable Long id,
                              @Valid @ModelAttribute("studyGroup") StudyGroup studyGroup,
                              BindingResult result,
                              HttpSession session,
                              Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        StudyGroup existing = studyGroupService.findById(id);
        if (existing == null) return "redirect:/my-groups";

        if (!userId.equals(existing.getCreatedBy().getId()))
            return "redirect:/my-groups";

        if (studyGroup.getMeetingMode() == StudyGroup.MeetingMode.IN_PERSON ||
            studyGroup.getMeetingMode() == StudyGroup.MeetingMode.HYBRID) {

            if (studyGroup.getMeetingLocation() == null ||
                studyGroup.getMeetingLocation().trim().isEmpty()) {

                result.rejectValue("meetingLocation", "error.meetingLocation",
                        "Meeting location is required for in-person or hybrid mode");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("levels", StudyGroup.KnowledgeLevel.values());
            model.addAttribute("frequencies", StudyGroup.MeetingFrequency.values());
            model.addAttribute("modes", StudyGroup.MeetingMode.values());
            model.addAttribute("branches", Branch.values());
            model.addAttribute("technologies", List.of());

            model.addAttribute("formattedStartTime",
                    studyGroup.getStartTime() != null ? studyGroup.getStartTime().format(HTML_DATETIME) : "");
            model.addAttribute("formattedEndTime",
                    studyGroup.getEndTime() != null ? studyGroup.getEndTime().format(HTML_DATETIME) : "");

            return "create-group";
        }

        studyGroup.setId(id);
        studyGroup.setCreatedBy(existing.getCreatedBy());

        studyGroupService.saveGroup(studyGroup);

        return "redirect:/my-groups?updated=true";
    }

    // ---------------------------------------------------------
    // MY GROUPS PAGE
    // ---------------------------------------------------------
    @GetMapping("/my-groups")
    public String myGroups(HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        User user = userService.findById(userId);

        List<StudyGroup> myCreatedGroups = studyGroupService.findAll().stream()
                .filter(g -> g.getCreatedBy() != null && g.getCreatedBy().getId().equals(userId))
                .toList();

        List<StudyGroup> myJoinedGroups = membershipService.getGroupsUserJoined(user).stream()
                .filter(g -> g.getCreatedBy() == null || !g.getCreatedBy().getId().equals(userId))
                .toList();

        model.addAttribute("myCreatedGroups", myCreatedGroups);
        model.addAttribute("myJoinedGroups", myJoinedGroups);

        return "my-study-groups";
    }

    // ---------------------------------------------------------
    // GROUP DETAILS PAGE (UPDATED WITH isAdmin)
    // ---------------------------------------------------------
    @GetMapping("/groups/{id}")
    public String groupDetails(@PathVariable Long id, HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);
        boolean isAdmin = LoginSession.isAdmin(session);

        StudyGroup group = studyGroupService.findById(id);
        if (group == null) return "redirect:/my-groups";

        User user = userService.findById(userId);

        boolean isMember = membershipService.isMember(user, group);
        boolean isLeader = group.getCreatedBy() != null &&
                           group.getCreatedBy().getId().equals(userId);

        int memberCount = membershipService.countMembers(group);
        List<User> members = membershipService.getMembersOfGroup(group);

        model.addAttribute("group", group);
        model.addAttribute("isMember", isMember);
        model.addAttribute("isLeader", isLeader);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("members", members);

        return "group-details";
    }

    // ---------------------------------------------------------
    // BROWSE GROUPS
    // ---------------------------------------------------------
    @GetMapping("/groups")
    public String browseGroups(Model model) {
        model.addAttribute("groups", studyGroupService.findAll());
        return "browse-groups";
    }

    // ---------------------------------------------------------
    // JOIN GROUP
    // ---------------------------------------------------------
    @PostMapping("/groups/{id}/join")
    public String joinGroup(@PathVariable Long id, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        StudyGroup group = studyGroupService.findById(id);
        if (group == null) return "redirect:/groups";

        User user = userService.findById(userId);

        String result = membershipService.joinGroup(user, group);

        return "redirect:/groups/" + id + "?msg=" + result;
    }

    // ---------------------------------------------------------
    // LEAVE GROUP
    // ---------------------------------------------------------
    @PostMapping("/groups/{id}/leave")
    public String leaveGroup(@PathVariable Long id, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        StudyGroup group = studyGroupService.findById(id);
        if (group == null) return "redirect:/groups";

        User user = userService.findById(userId);

        membershipService.leaveGroup(user, group);

        return "redirect:/groups/" + id + "?left=true";
    }

    // ---------------------------------------------------------
    // DELETE GROUP (LEADER ONLY)
    // ---------------------------------------------------------
    @PostMapping("/groups/{id}/delete")
    public String deleteGroupAsLeader(@PathVariable Long id, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        Long userId = LoginSession.getUserId(session);

        StudyGroup group = studyGroupService.findById(id);

        if (group == null || group.getCreatedBy() == null ||
            !group.getCreatedBy().getId().equals(userId)) {
            return "redirect:/dashboard?error=not-authorized";
        }

        studyGroupService.deleteGroup(id);

        return "redirect:/my-groups?deleted=true";
    }
}
