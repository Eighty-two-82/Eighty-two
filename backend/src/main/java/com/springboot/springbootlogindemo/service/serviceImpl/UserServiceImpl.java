package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public User loginService(String uname, String password) {
        // If both account and password are correct, return the logged-in user object, if either is wrong return null
        User user = userDao.findByUnameAndPassword(uname, password);
        // Clear sensitive information
        if(user != null){
            user.setPassword("");
        }
        return user;
    }

    @Override
    public User registService(User user) {
        // When the username of the new user already exists
        if(userDao.findByUname(user.getUname())!=null){
            // Cannot register
            return null;
        }else{
            // Return the created user object (with uid)
            User newUser = userDao.save(user);
            if(newUser != null){
                newUser.setPassword("");
            }
            return newUser;
        }
    }
}
