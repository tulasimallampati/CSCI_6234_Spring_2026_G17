package com.learnmesh.entity;


import jakarta.servlet.http.HttpSession;

public class LoginSession {

    public static Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    public static String getRole(HttpSession session) {
        return (String) session.getAttribute("role");
    }

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public static boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    public static void logout(HttpSession session) {
        session.invalidate();
    }
}
