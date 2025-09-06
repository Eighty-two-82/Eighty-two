package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.service.UserService;
import com.springboot.springbootlogindemo.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<User> loginController(@RequestBody User loginUser){
        User user = userService.loginService(loginUser.getUname(), loginUser.getPassword());
        if(user != null){
            return Result.success(user,"Login successful!");
        } else {
            return Result.error("123","Account or password incorrect!");
        }
    }

    @PostMapping("/register")
    public Result<User> registController(@RequestBody User newUser){
        User user = userService.registService(newUser);
        if(user != null){
            return Result.success(user,"Registration successful!");
        } else {
            return Result.error("456","Username already exists!");
        }
    }
}
