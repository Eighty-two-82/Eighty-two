package com.careapp.service.impl;

import com.careapp.domain.User;
import com.careapp.repository.UserRepository;
import com.careapp.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

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
    @Override
    public String getWorkerStatus(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "not_found";
        }
        return user.getStatus();
    }
    @Override
    public boolean bindWorkerToPatient(String workerId, String patientId) {
        User worker = userRepository.findById(workerId).orElse(null);
        if (worker == null) return false;
        worker.setPatientId(patientId);
        worker.setStatus("bound");
        userRepository.save(worker);
        return true;
    }

    @Override
    public String getWorkerPatient(String userId) {
        User worker = userRepository.findById(userId).orElse(null);
        if (worker == null) return null;
        return worker.getPatientId();
    }


    @Override
    public boolean changePassword(String identifier, String oldPassword, String newPassword) {
        if (!StringUtils.hasText(identifier) || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        User user = userRepository.findByEmailAndPassword(identifier, oldPassword);
        if (user == null) {
            user = userRepository.findByUnameAndPassword(identifier, oldPassword);
        }
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    @Override
    public String createPasswordResetToken(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            return null;
        }
        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            user = userRepository.findByUname(identifier);
        }
        if (user == null) {
            return null;
        }
        String token = generateToken();
        long expires = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minutes
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(expires);
        userRepository.save(user);
        return token;
    }

    @Override
    public boolean resetPasswordByToken(String token, String newPassword) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        User user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            return false;
        }
        Long expires = user.getPasswordResetExpires();
        if (expires == null || expires < System.currentTimeMillis()) {
            return false;
        }
        user.setPassword(newPassword);
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
        return true;
    }

    private String generateToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }




}