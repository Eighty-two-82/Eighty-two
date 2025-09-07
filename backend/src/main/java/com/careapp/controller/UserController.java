package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.service.UserService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<User> loginController(@RequestBody User user) {
        User u = userService.loginService(user.getUname(), user.getPassword());
        if (u != null) {
            return Result.success(u, "Login successful!");
        } else {
            return Result.error("123", "Account or password incorrect!");
        }
    }

    @PostMapping("/register")
    public Result<User> registerController(@RequestBody User newUser) {
        User user = userService.registService(newUser);
        if (user != null) {
            return Result.success(user, "Registration successful!");
        } else {
            return Result.error("456", "Username already exists!");
        }
    }
}
