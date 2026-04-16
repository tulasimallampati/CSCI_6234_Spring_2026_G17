package com.learnmesh.service;

import com.learnmesh.entity.User;
import com.learnmesh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public User validateLogin(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        // Later we will replace this with BCrypt
        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }
}
