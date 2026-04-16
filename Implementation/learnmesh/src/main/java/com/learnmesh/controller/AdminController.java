package com.learnmesh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.learnmesh.entity.LoginSession;
import com.learnmesh.service.StudyGroupService;
import com.learnmesh.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyGroupService groupService;

    // ---------------------------------------------------------
    // ADMIN DASHBOARD
    // ---------------------------------------------------------
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        if (!LoginSession.isAdmin(session)) return "redirect:/dashboard";

        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalGroups", groupService.countGroups());
        model.addAttribute("activeGroups", groupService.countActiveGroups());
        model.addAttribute("fullGroups", groupService.countFullGroups());

        return "admin-dashboard";
    }

    // ---------------------------------------------------------
    // MANAGE USERS
    // ---------------------------------------------------------
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        if (!LoginSession.isAdmin(session)) return "redirect:/dashboard";

        model.addAttribute("users", userService.findAll());
        return "admin-users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        if (!LoginSession.isAdmin(session)) return "redirect:/dashboard";

        userService.deleteUser(id);
        return "redirect:/admin/users?deleted=true";
    }

    // ---------------------------------------------------------
    // MANAGE GROUPS
    // ---------------------------------------------------------
    @GetMapping("/groups")
    public String manageGroups(HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        if (!LoginSession.isAdmin(session)) return "redirect:/dashboard";

        model.addAttribute("groups", groupService.findAll());
        return "admin-groups";
    }

    @PostMapping("/groups/{id}/delete")
    public String deleteGroup(@PathVariable Long id, HttpSession session) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        if (!LoginSession.isAdmin(session)) return "redirect:/dashboard";

        groupService.deleteGroup(id);
        return "redirect:/admin/groups?deleted=true";
    }

    // ---------------------------------------------------------
    // VIEW USER DETAILS (Groups Created + Groups Joined)
    // ---------------------------------------------------------
    @GetMapping("/users/{id}")
    public String viewUserDetails(@PathVariable Long id, HttpSession session, Model model) {

        if (!LoginSession.isLoggedIn(session)) return "redirect:/login";
        if (!LoginSession.isAdmin(session)) return "redirect:/dashboard";

        var user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/users?error=UserNotFound";
        }

        model.addAttribute("user", user);
        model.addAttribute("groupsCreated", groupService.findGroupsByLeader(user.getId()));
        model.addAttribute("groupsJoined", groupService.findGroupsUserJoined(user.getId()));

        return "admin-user-details";
    }
}
