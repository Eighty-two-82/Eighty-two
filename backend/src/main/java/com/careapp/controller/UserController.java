package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.service.UserService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Resource
    private UserService userService;

    // based on uname
    @PostMapping("/login")
    public Result<User> loginController(@RequestBody User user) {
        User u = userService.loginService(user.getUname(), user.getPassword());
        if (u != null) {
            return Result.<User>success(u, "Login successful!");
        } else {
            return Result.<User>error("401", "Invalid username or password!");
        }
    }

    // based on email
    @PostMapping("/login-email")
    public Result<User> loginByEmailController(@RequestBody User user) {
        User u = userService.loginByEmailService(user.getEmail(), user.getPassword());
        if (u != null) {
            return Result.<User>success(u, "Login successful!");
        } else {
            return Result.<User>error("401", "Invalid email or password!");
        }
    }

    @PostMapping("/register")
    public Result<User> registerController(@RequestBody User newUser) {
        User user = userService.registerService(newUser);
        if (user != null) {
            return Result.<User>success(user, "Registration successful!");
        } else {
            return Result.<User>error("409", "Email already exists!");
        }
    }
}
