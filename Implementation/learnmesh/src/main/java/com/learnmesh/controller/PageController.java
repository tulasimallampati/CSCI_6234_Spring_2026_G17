package com.learnmesh.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.learnmesh.entity.User;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", "home");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("page", "about");
        return "about";
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("page", "login");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("page", "register");
        model.addAttribute("user", new User());   // REQUIRED
        return "register";
    }

    
  

}
