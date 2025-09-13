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
            user.setPassword(null); // avoid returning sensitive information
        }
        return user;
    }

    @Override
    public User loginByEmailService(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            user.setPassword(null); // avoid returning sensitive information
        }
        return user;
    }

    @Override
    public User registerService(User user) {
        // check if email is already in use
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return null; // email already in use
        }
        
        // set uname to email (backward compatibility)
        user.setUname(user.getEmail());
        
        User newUser = userRepository.save(user);
        if (newUser != null) {
            newUser.setPassword(null);
        }
        return newUser;
    }
}