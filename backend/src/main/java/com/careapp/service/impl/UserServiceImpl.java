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
            user.setPassword(null); // 避免返回敏感信息
        }
        return user;
    }

    @Override
    public User registerService(User user) {
        if (userRepository.findByUname(user.getUname()) != null) {
            return null; // 已存在同名用户
        }
        User newUser = userRepository.save(user);
        if (newUser != null) {
            newUser.setPassword(null);
        }
        return newUser;
    }
}
