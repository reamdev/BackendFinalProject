package com.backend.backendfinalproject.controller;

import com.backend.backendfinalproject.models.request.LoginRequest;
import com.backend.backendfinalproject.models.request.LoginResponse;
import com.backend.backendfinalproject.models.User;
import com.backend.backendfinalproject.models.response.UserResponse;
import com.backend.backendfinalproject.repositories.interfaces.IUserRepository;
import com.backend.backendfinalproject.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthController {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JWTUtil jwt;

    @PostMapping(value = "api/login")
    public LoginResponse login(@RequestBody LoginRequest user) {
        LoginResponse login = new LoginResponse();
        login.setResult(false);
        UserResponse loggedUser = userRepository.getUserByCredentials(user);
        if (loggedUser != null) {
            // Create Token
            String token = jwt.create(String.valueOf(loggedUser.getId()), String.valueOf(loggedUser.getId()));
            login.setToken(token);
            login.setAbv((""+loggedUser.getName().toCharArray()[0]+loggedUser.getLastName().toCharArray()[0]).toUpperCase());
            login.setResult(true);
        }
        return login;
    }
}
