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

    @PostMapping("/login")
    public Result<User> loginController(@RequestBody User user) {
        User u = userService.loginService(user.getUname(), user.getPassword());
        if (u != null) {
            return Result.success(u, "Login successful!");
        } else {
            return Result.error("401", "Invalid username or password!");
        }
    }

    @PostMapping("/register")
    public Result<User> registerController(@RequestBody User newUser) {
        User user = userService.registerService(newUser);
        if (user != null) {
            return Result.success(user, "Registration successful!");
        } else {
            return Result.error("409", "Username already exists!");
        }
    }
}
