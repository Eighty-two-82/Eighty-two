package com.careapp.service.impl;

import com.careapp.domain.User;
import com.careapp.repository.UserRepository;
import com.careapp.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository userRepository;

    @Override
    public User loginService(String uname, String password) {
        User user = userRepository.findByUnameAndPassword(uname, password);
        if (user != null) {
            user.setPassword(""); // 清空敏感信息
        }
        return user;
    }

    @Override
    public User registService(User user) {
        if (userRepository.findByUname(user.getUname()) != null) {
            return null;
        } else {
            User newUser = userRepository.save(user);
            if (newUser != null) {
                newUser.setPassword("");
            }
            return newUser;
        }
    }
}
