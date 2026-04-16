package com.learnmesh.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // Protect dashboard
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        // Pass user name to dashboard
        String name = (String) session.getAttribute("userName");
        model.addAttribute("name", name);

        return "dashboard";
    }
}
