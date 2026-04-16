package com.learnmesh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learnmesh.entity.LoginSession;
import com.learnmesh.entity.User;
import com.learnmesh.service.LoginService;
import com.learnmesh.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    // ---------------------------------------------------------
    // REGISTER USER (Always STUDENT)
    // ---------------------------------------------------------
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "register";
        }

        // ⭐ Force all registered users to be STUDENT
        user.setRole(User.Role.STUDENT);

        userService.saveUser(user);

        return "redirect:/login?success=registered";
    }

    // ---------------------------------------------------------
    // LOGIN USER (Admin or Student)
    // ---------------------------------------------------------
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = loginService.validateLogin(email, password);

        if (user == null) {
            model.addAttribute("loginError", "Invalid email or password");
            model.addAttribute("user", new User());
            return "login";
        }

        // ⭐ Store user info in session (CORRECTED)
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getFirstName());
        session.setAttribute("role", user.getRole().name());

        // ⭐ Redirect Admins to Admin Dashboard
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        // ⭐ Students go to normal dashboard
        return "redirect:/dashboard";
    }

    // ---------------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        LoginSession.logout(session);
        return "redirect:/login?logout=true";
    }
}
